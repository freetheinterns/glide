package common.glide.slideshow

import common.glide.gui.panels.FullScreenFrame
import common.glide.storage.ENV
import common.glide.storage.KEY_BINDINGS
import common.glide.utils.extensions.CACHED_FILE
import common.glide.utils.extensions.CACHE_FULL_IMAGE
import common.glide.utils.extensions.CACHE_RESIZED_IMAGE
import common.glide.utils.extensions.always
import common.glide.utils.extensions.blindObserver
import common.glide.utils.extensions.cache
import common.glide.utils.extensions.catalogs
import common.glide.utils.extensions.chooseBestDisplayMode
import common.glide.utils.extensions.dimension
import common.glide.utils.extensions.imageCount
import common.glide.utils.extensions.logger
import common.glide.utils.extensions.superGC
import common.glide.utils.extensions.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Color
import java.awt.Graphics
import java.awt.GraphicsEnvironment
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.exitProcess

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
  val library: Array<Catalog> by always { _library!! }
  var scaling: Int = ENV.scaling
    set(value) {
      logger.info("Updating scaling from: ${ENV.scaling}, to: $value")
      ENV.scaling = value
      index.current.rerender()
      index.copy.next().rerender()
      project()
      field = value
    }

  private var _index: ImageIndex? by cache { ImageIndex(library) }
  private var _library: Array<Catalog>? by cache { File(ENV.root).catalogs }

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)
  private var timer = Timer(ENV.speed) { KEY_BINDINGS.trigger("pageForward") }

  init {
    if (!device.isFullScreenSupported) throw IllegalArgumentException("Non full-screen modes not yet supported")

    // Set up Listeners
    defaultCloseOperation = DO_NOTHING_ON_CLOSE
    addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        super.windowClosed(e)
        exit()
      }
    })
    addMouseListener(EventHandler)
    EventHandler.register()
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
    ENV.scope = "Projector"

    // Short-circuit if playlist is empty or if full screen is not possible
    if (library.map { it.size }.sum() == 0) {
      logger.severe("No images found to display!")
      exit(1)
    }

    project()
  }

  fun exit(status: Int = 0) {
    EventHandler.deregister()
    ENV.projector = null
    device.fullScreenWindow = null
    exitProcess(status)
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
    bufferStrategy.drawGraphics.use {
      wipeScreen(it)
      painter(it)
    }
    bufferStrategy.show()
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

  private fun constructGeometry(
    vararg pages: ImageIndex
  ): Array<Geometry> {

    var margin = size.width
      .minus(pages.sumBy { it.current.width })
      .div(2)

    val pageGeometry: Array<Geometry> = pages.run {
      if (ENV.direction) reversed() else toList()
    }.map {
      it.current.build(margin).apply {
        margin += it.current.width
      }
    }.toTypedArray()

    return arrayOf(
      *pageGeometry,
      marginPanel.build()
    )
  }

  private fun project() {
    val pages: MutableList<ImageIndex> = mutableListOf()
    var margin = size.width
    var focus = index.copy

    do {
      margin -= focus.current.width
      if (margin < 0) break
      pages.add(focus)
      focus = focus + 1
    } while (
      ENV.paneled &&                     // Panelling is enabled
      focus.primary == index.primary &&  // We are still in the current Catalog
      pages.size < ENV.maxImagesPerFrame // Honor upper bound
    )

    geometry = constructGeometry(*pages.toTypedArray())
  }

  private fun render() {
    drawPage { g -> geometry.forEach { it.paint(g) } }
    if (geometry.isNotEmpty()) updateCaching()
  }

  private fun updateCaching() {
    val cacheFront = index - max(0, index.secondary - ENV.maxImagesPerFrame * 2)
    while (cacheFront.hasNext()) {
      val offset = abs(cacheFront.compareTo(index))
      if (offset > ENV.maxImagesPerFrame * 2)
        return

      GlobalScope.launch(Dispatchers.IO) {
        when {
          offset < ENV.maxImagesPerFrame     -> cacheFront.current.cacheLevel = CACHE_RESIZED_IMAGE
          offset < ENV.maxImagesPerFrame + 2 -> cacheFront.current.cacheLevel = CACHE_FULL_IMAGE
          else                               -> cacheFront.current.cacheLevel = CACHED_FILE
        }
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
      _library = library.filter {
        !it.path.startsWith(target.absolutePath)
      }.toTypedArray()

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
      logger.warning("Deleting Folder: ${it.absolutePath}")
      it.deleteRecursively()
    }
  }

  fun archiveCurrentDirectory() {
    purgeCatalog(index.primary) {
      val newPath = File("${ENV.archive}\\${it.name}").toPath()
      logger.warning("Moving Folder: ${it.absolutePath} --> $newPath")
      if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
        logger.severe("Target Path already exists! No action taken!")
      else
        Files.move(it.toPath(), newPath)
    }
  }
}
