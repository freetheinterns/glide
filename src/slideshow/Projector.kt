package slideshow

import storage.ENV
import utils.extensions.CACHED_FILE
import utils.extensions.CACHE_FULL_IMAGE
import utils.extensions.CACHE_RESIZED_IMAGE
import utils.extensions.blindObserver
import utils.extensions.chooseBestDisplayMode
import utils.extensions.dimension
import utils.extensions.lazyCache
import utils.extensions.listMatchingDirectories
import utils.extensions.superGC
import utils.extensions.vprintln
import utils.inheritors.Geometry
import utils.inheritors.FullScreenFrame
import java.awt.Color
import java.awt.Graphics
import java.awt.GraphicsEnvironment
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import javax.swing.JFrame
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max


class Projector : FullScreenFrame(), Iterable<CachedImage> {
  ///////////////////////////////////////
  // Properties
  ///////////////////////////////////////

  var focus: CachedImage?      by blindObserver(null, ::project)
  private var idx: ImageIndex?        by lazyCache { ImageIndex(library!!) }
  val index: ImageIndex
    get() = idx!!
  private var itemCount: Int?          by lazyCache { library!!.map { it.size }.sum() }
  private var geometry by blindObserver(arrayOf<Geometry>(), ::render)
  var library: List<Playlist>? by lazyCache {
    File(ENV.root).listMatchingDirectories().map { Playlist(it, this) }.sorted()
  }

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val handler = EventHandler(this)
  private var timer = Timer(ENV.speed, handler)
  private val marginPanel = MarginPanel(this)
  val imageGeometryCount: Int
    get() = geometry.count { it.geometryType == "slideshow.CachedImage" }

  init {
    // Short-circuit if playlist is empty or if full screen is not possible
    if (itemCount == 0) {
      println("No images found to display!")
      System.exit(1)
    }
    if (!device.isFullScreenSupported) {
      println("Full screen mode not supported")
      System.exit(1)
    }

    // Set up Listeners
    defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    addWindowListener(handler)
    addMouseListener(handler)
    addKeyListener(handler)
    timer.initialDelay = ENV.speed

    // Set up JFrame
    ignoreRepaint = true
    isResizable = false
    isUndecorated = true
    focusTraversalKeysEnabled = false

    // Set JFrame to full screen
    device.fullScreenWindow = this
    device.chooseBestDisplayMode()
    size = device.displayMode.dimension

    // Use a dual imageBufferCapacity strategy for Graphics control
    createBufferStrategy(2)

    // Draw initial black background
    drawPage {}

    // Update caching states of nearby images
    updateCaching()
  }

  fun exit(status: Int = 0) {
    device.fullScreenWindow = null
    System.exit(status)
  }

  override fun iterator() = ImageIndex(library!!)

  private fun clearCaches(): Int {
    drawPage {}
    val prim = index.primary
    idx = null
    focus = null
    geometry = arrayOf()
    itemCount = null
    library = null
    superGC(50)
    return prim
  }

  override fun toString(): String = "<slideshow.Projector: ${hashCode()}>"

  ///////////////////////////////////////
  // Draw Logic
  ///////////////////////////////////////

  private fun wipeScreen(g: Graphics, color: Color = Color.BLACK) {
    val oldColor = g.color
    g.color = color
    g.fillRect(0, 0, size.width, size.height)
    g.color = oldColor
  }

  private inline infix fun drawPage(crossinline painter: (Graphics) -> Unit) {
    val g = bufferStrategy.drawGraphics
    wipeScreen(g)
    painter(g)
    bufferStrategy.show()
    g.dispose()
  }

  private fun render() {
    drawPage { g ->
      geometry.forEach { it.paint(g) }
    }
  }

  private fun project() {
    if (focus == null) return
    if (itemCount!! > 1 && ENV.paneled && index.secondary < index.maxSecondary!! - 1) {
      val next = index + 1
      val margin = (size.width - index.current!!.width - next.current!!.width) / 2
      if (margin >= 0) {
        geometry = arrayOf(
          index.current!!.build(size.width - margin - index.current!!.width),
          next.current!!.build(margin),
          marginPanel.build(margin)
        )
        return
      }
    }
    geometry = arrayOf(
      index.current!!.build((size.width / 2) - (index.current!!.width / 2)),
      marginPanel.build((size.width - index.current!!.width) / 2)
    )
  }

  fun updateCaching() {
    focus = index.current
    val cacheFront = index - max(0, index.secondary - ENV.intraPlaylistVision)
    while (cacheFront.hasNext()) {
      val diff = cacheFront.compareTo(index)
      val offset = abs(diff)
      when {
        offset < ENV.imageBufferCapacity     -> cacheFront.current!!.cacheLevel = CACHE_RESIZED_IMAGE
        offset < ENV.imageBufferCapacity + 1 -> cacheFront.current!!.cacheLevel = CACHE_FULL_IMAGE
        diff > ENV.intraPlaylistVision       -> return
        else                                 -> cacheFront.current!!.cacheLevel = CACHED_FILE
      }
      cacheFront.increment(1)
    }
  }

  ///////////////////////////////////////
  // Image Iterator Helpers
  ///////////////////////////////////////

  fun dumbNext() {
    index.increment(1)
    updateCaching()
  }

  fun previous() {
    index.decrement(1)
    updateCaching()
  }

  fun next() {
    index.increment(imageGeometryCount)
    updateCaching()
  }

  fun prev() {
    if (ENV.paneled) {
      index.decrement(2)
    } else {
      index.decrement(1)
    }
    updateCaching()
  }

  fun nextFolder() {
    index.jump(1)
    updateCaching()
  }

  fun prevFolder() {
    index.jump(-1)
    updateCaching()
  }

  fun toggleTimer() {
    if (timer.isRunning) {
      timer.stop()
    } else {
      timer.start()
      next()
    }
  }

  private fun softJump(target: Int) =
    when (index.maxPrimary) {
      0            -> exit()
      in 0..target -> {
        index.primary = index.maxPrimary - 1
      }
      else         -> {
        index.primary = target
      }
    }

  ///////////////////////////////////////
  // File Helpers
  ///////////////////////////////////////

  fun deleteCurrentDirectory() {
    val targetPosition = index.primary
    val target = library!![targetPosition].file
    vprintln("Deleting Folder: ${target.absolutePath}")
    clearCaches()

    target.deleteRecursively()
    superGC(50)

    // Reset library, index & caches
    softJump(targetPosition)
    updateCaching()
    vprintln("Jumping to index $index")
  }

  fun archiveCurrentDirectory() {
    val targetPosition = index.primary
    val target = library!![targetPosition].file
    val newPath = File("${ENV.archive}\\${target.name}").toPath()
    vprintln("Moving Folder: ${target.absolutePath} --> $newPath")
    if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS)) {
      vprintln("Target Path already exists! No action taken!")
      return
    }
    clearCaches()

    Files.move(target.toPath(), newPath)
    superGC(50)

    // Reset library, index & caches
    softJump(targetPosition)
    updateCaching()
  }
}