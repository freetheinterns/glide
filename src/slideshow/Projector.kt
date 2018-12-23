package slideshow

import storage.ENV
import utils.extensions.CACHED_FILE
import utils.extensions.CACHE_FULL_IMAGE
import utils.extensions.CACHE_RESIZED_IMAGE
import utils.extensions.always
import utils.extensions.blindObserver
import utils.extensions.cache
import utils.extensions.catalogs
import utils.extensions.chooseBestDisplayMode
import utils.extensions.dimension
import utils.extensions.imageCount
import utils.extensions.superGC
import utils.extensions.vprintln
import utils.inheritors.FullScreenFrame
import utils.inheritors.Geometry
import java.awt.Color
import java.awt.Graphics
import java.awt.GraphicsEnvironment
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import javax.swing.JFrame
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max

/**
 * @property geometry Array<Geometry>
 * @property index ImageIndex The current image in the library being displayed
 * @property _index ImageIndex? The private cache for index
 * @property library List<Catalog> The list of Catalogs loaded by the program
 * @property _library List<Catalog>? The private cache for library
 * @property device GraphicsDevice
 * @property marginPanel MarginPanel
 * @property timer Timer
 */
class Projector : FullScreenFrame(), Iterable<CachedImage> {
  ///////////////////////////////////////
  // Properties
  ///////////////////////////////////////

  var geometry by blindObserver(arrayOf<Geometry>(), ::render)
  val index: ImageIndex by always { _index!! }
  val library: List<Catalog> by always { _library!! }
  var scaling: Int = ENV.scaling
    set(value) {
      vprintln("Updating scaling to: ${ENV.scaling}")
      ENV.scaling = value
      index.current.rerender()
      index.copy.next().rerender()
      updateCaching()
    }

  private var _index: ImageIndex? by cache { ImageIndex(library) }
  private var _library: List<Catalog>? by cache { File(ENV.root).catalogs }

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)
  private var timer = Timer(ENV.speed, EventHandler)

  init {
    if (!device.isFullScreenSupported) throw IllegalArgumentException("Non full-screen modes not yet supported")

    // Set up Listeners
    defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
    addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        super.windowClosed(e)
        exit()
      }
    })
    addMouseListener(EventHandler)
    addKeyListener(EventHandler)
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

    // IMPORTANT!! Register the screen globally
    ENV.projector = this

    // Short-circuit if playlist is empty or if full screen is not possible
    if (library.map { it.size }.sum() == 0) {
      println("No images found to display!")
      exit(1)
    }

    project()
  }

  fun exit(status: Int = 0) {
    device.fullScreenWindow = null
    System.exit(status)
  }

  override fun iterator() = ImageIndex(library)

  override fun toString(): String = "<Projector: ${hashCode()}>"

  ///////////////////////////////////////
  // Draw Logic Helpers
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

  ///////////////////////////////////////
  // Draw Logic
  ///////////////////////////////////////
  // Order of operations:
  // - The index is modified and project() is called
  // - project() recalculates the geometry array which triggers...
  // - render() draws all geometry to a screen and triggers...
  // - updateCaching() which re-evaluates the caching states of buffered images
  ///////////////////////////////////////

  private fun project() {
    if (ENV.paneled && index.secondary < index.maxSecondary - 1) {
      val next = index + 1
      val margin = (size.width - index.current.width - next.current.width) / 2
      if (margin >= 0) {
        geometry = arrayOf(
          index.current.build(size.width - margin - index.current.width),
          next.current.build(margin),
          marginPanel.build(margin)
        )
        return
      }
    }
    geometry = arrayOf(
      index.current.build((size.width / 2) - (index.current.width / 2)),
      marginPanel.build((size.width - index.current.width) / 2)
    )
  }

  private fun render() {
    drawPage { g -> geometry.forEach { it.paint(g) } }
    if (geometry.isNotEmpty()) updateCaching()
  }

  private fun updateCaching() {
    val cacheFront = index - max(0, index.secondary - ENV.intraPlaylistVision)
    while (cacheFront.hasNext()) {
      val offset = abs(cacheFront.compareTo(index))
      when {
        offset < ENV.imageBufferCapacity     -> cacheFront.current.cacheLevel = CACHE_RESIZED_IMAGE
        offset < ENV.imageBufferCapacity + 1 -> cacheFront.current.cacheLevel = CACHE_FULL_IMAGE
        offset > ENV.intraPlaylistVision     -> return
        else                                 -> cacheFront.current.cacheLevel = CACHED_FILE
      }
      cacheFront.next()
    }
  }

  ///////////////////////////////////////
  // Image Iterator Helpers
  ///////////////////////////////////////

  fun dumbNext() {
    index += 1
    project()
  }

  fun previous() {
    index -= 1
    project()
  }

  fun next() {
    index += geometry.imageCount
    project()
  }

  fun prev() {
    index -= 1
    if (ENV.paneled) {
      var realestate = width - index.current.width
      var lookahead = index - 1
      while (realestate >= lookahead.current.width && lookahead.primary == index.primary) {
        index -= 1
        realestate -= index.current.width
        lookahead = index - 1
      }
    }
    project()
  }

  fun nextFolder() {
    index.jump(1)
    project()
  }

  fun prevFolder() {
    index.jump(-1)
    project()
  }

  fun toggleTimer() {
    if (timer.isRunning) {
      timer.stop()
    } else {
      timer.start()
      next()
    }
  }

  private fun softJump(target: Int) {
    when (index.maxPrimary) {
      0            -> exit()
      in 0..target -> {
        index.primary = index.maxPrimary - 1
      }
      else         -> {
        index.primary = target
      }
    }
    project()
  }

  ///////////////////////////////////////
  // File Helpers
  ///////////////////////////////////////


  private inline fun purgeCatalog(
    targetPosition: Int,
    jumpPosition: Int = targetPosition,
    crossinline operation: (File) -> Unit
  ) {

    drawPage {}

    try {
      val target = library[targetPosition].file

      _index = null
      _library = library.filter { !it.path.startsWith(target.absolutePath) }
      geometry = arrayOf()

      operation(target)
    } catch (err: Exception) {
      throw err
    }

    superGC(50)

    softJump(jumpPosition)
  }

  fun deleteCurrentDirectory() {
    purgeCatalog(index.primary) {
      vprintln("Deleting Folder: ${it.absolutePath}")
      it.deleteRecursively()
    }
  }

  fun archiveCurrentDirectory() {
    purgeCatalog(index.primary) {
      val newPath = File("${ENV.archive}\\${it.name}").toPath()
      vprintln("Moving Folder: ${it.absolutePath} --> $newPath")
      if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
        vprintln("Target Path already exists! No action taken!")
      else
        Files.move(it.toPath(), newPath)
    }
  }
}
