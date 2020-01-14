package common.glide.slideshow

import common.glide.ENV
import common.glide.KEY_BINDINGS
import common.glide.extensions.CACHED_FILE
import common.glide.extensions.CACHE_FULL_IMAGE
import common.glide.extensions.CACHE_RESIZED_IMAGE
import common.glide.extensions.blindObserver
import common.glide.extensions.catalogs
import common.glide.extensions.chooseBestDisplayMode
import common.glide.extensions.dimension
import common.glide.extensions.imageCount
import common.glide.extensions.logger
import common.glide.extensions.use
import common.glide.gui.listeners.EventHandler
import common.glide.gui.panels.FullScreenFrame
import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidateCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Graphics2D
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
import kotlin.system.measureTimeMillis

/**
 * @property geometry Array<Geometry>
 * @property index ImageIndex The current image in the library being displayed
 * @property library List<Catalog> The list of Catalogs loaded by the program
 * @property device GraphicsDevice
 * @property marginPanel MarginPanel
 * @property timer Timer
 */
class Projector : FullScreenFrame(), Iterable<CachedImage> {
  companion object {
    private val log by logger()
  }

  ///////////////////////////////////////
  // Properties
  ///////////////////////////////////////

  var geometry by blindObserver(arrayOf<Geometry>(), ::render)
  val index: ImageIndex by cache { ImageIndex(library) }
  var library: Array<Catalog> by cache { File(ENV.root).catalogs }

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)
  private var timer = Timer(ENV.speed) { KEY_BINDINGS.trigger("pageForward") }

  init {
    if (!device.isFullScreenSupported)
      throw IllegalArgumentException("Full-screen modes not yet supported on device")

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
    drawPage()

    // IMPORTANT!! Register the screen globally
    ENV.projector = this
    ENV.scope = "Projector"

    // Short-circuit if playlist is empty or if full screen is not possible
    if (library.map { it.size }.sum() == 0) {
      log.severe("No images found to display!")
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

  private fun wipeScreen(g: Graphics2D, color: Color = Color.BLACK) {
    val oldColor = g.color
    g.color = color
    g.fillRect(0, 0, size.width, size.height)
    g.color = oldColor
  }

  private fun preRender(g: Graphics2D) {
    val focus = index + geometry.imageCount

    repeat(ENV.maxImagesPerFrame) {
      focus.current.build(size.width * 2, size.height * 2).render(g)
      focus.inc()
    }

    wipeScreen(g)
  }

  private fun drawPage(painter: ((Graphics2D) -> Unit)? = null) {
    val drawTime = measureTimeMillis {
      (bufferStrategy.drawGraphics as Graphics2D).use {
        wipeScreen(it)
        painter?.invoke(it)
      }
      bufferStrategy.show()
    }

    log.info("Spent $drawTime ms drawing")
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
    pages: List<ImageIndex>
  ): Array<Geometry> {
    var margin = (size.width - pages.sumBy { it.current.width }) / 2

    val pageGeometry =
      pages.run {
        if (ENV.direction)
          reversed()
        else
          toList()
      }.map {
        it
          .current
          .build(margin, (size.height - it.current.height) / 2)
          .apply { margin += it.current.width }
      }

    log.info("geo: ${System.currentTimeMillis()}")
    return arrayOf(*pageGeometry.toTypedArray(), marginPanel.build())
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

    geometry = constructGeometry(pages)
  }


  private fun render() {
    drawPage { g -> geometry.forEach { it.render(g) } }

    // Launch coroutine and delay to allow graphics thread to render
    runBlocking {
      delay(10)

      drawPage { g ->
        preRender(g)
        geometry.forEach { it.render(g) }
      }

      updateCaching()
    }
  }

  private fun updateCaching() {
    if (geometry.isEmpty()) return
    val cacheFront = index - max(0, index.secondary - ENV.maxImagesPerFrame * 4)
    while (cacheFront.hasNext()) {
      val offset = abs(cacheFront.compareTo(index))
      if (offset > ENV.maxImagesPerFrame * 4)
        return

      GlobalScope.launch(Dispatchers.IO) {
        when {
          offset < ENV.maxImagesPerFrame * 2 -> cacheFront.current.cacheLevel =
            CACHE_RESIZED_IMAGE
          offset < ENV.maxImagesPerFrame * 3 -> cacheFront.current.cacheLevel =
            CACHE_FULL_IMAGE
          else                               -> cacheFront.current.cacheLevel =
            CACHED_FILE
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

    drawPage()

    try {
      val target = library[targetPosition].file

      invalidateCache(::index)
      library = library.filter {
        !it.path.startsWith(target.absolutePath)
      }.toTypedArray()

      geometry = arrayOf()

      operation(target)
    } catch (err: Exception) {
      throw err
    }

    System.gc()

    softJump(jumpPosition)
  }

  fun deleteCurrentDirectory() {
    purgeCatalog(index.primary) {
      log.warning("Deleting Folder: ${it.absolutePath}")
      it.deleteRecursively()
    }
  }

  fun archiveCurrentDirectory() {
    purgeCatalog(index.primary) {
      val newPath = File("${ENV.archive}\\${it.name}").toPath()
      log.warning("Moving Folder: ${it.absolutePath} --> $newPath")
      if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
        log.severe("Target Path already exists! No action taken!")
      else
        Files.move(it.toPath(), newPath)
    }
  }
}
