package common.glide.slideshow

import common.glide.ENV
import common.glide.Extension
import common.glide.Operation
import common.glide.extensions.catalogs
import common.glide.extensions.chooseBestDisplayMode
import common.glide.extensions.dimension
import common.glide.extensions.fitCentered
import common.glide.extensions.imageCount
import common.glide.extensions.logger
import common.glide.extensions.times
import common.glide.extensions.use
import common.glide.gui.listeners.EventHandler
import common.glide.gui.panels.FullScreenFrame
import common.glide.quit
import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidate
import common.glide.utils.TriggeringProperty
import kotlinx.coroutines.delay
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
import kotlin.system.measureTimeMillis


class Projector : FullScreenFrame() {
  companion object {
    var singleton: Projector? = null
    private val log by logger()
  }

  ///////////////////////////////////////
  // Properties
  ///////////////////////////////////////

  var geometry by TriggeringProperty(listOf<Geometry>(), ::render)
  var library: List<Catalog> by cache { File(ENV.root).catalogs }
  val index: ImageIndex by cache { ImageIndex(library) }
  var frameCount: Int = 0

  private var timer = Timer(ENV.speed) { next() }
  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)

  init {
    if (!device.isFullScreenSupported)
      throw IllegalArgumentException("Full-screen modes not yet supported on device")

    // Set up Listeners
    defaultCloseOperation = DO_NOTHING_ON_CLOSE
    addWindowListener(object : WindowAdapter() {
      override fun windowClosed(e: WindowEvent?) {
        super.windowClosed(e)
        quit(0)
      }
    })
    addMouseListener(EventHandler)
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
    singleton = this

    // Short-circuit if playlist is empty or if full screen is not possible
    if (library.map { it.size }.sum() == 0) {
      log.severe("No images found to display!")
      quit(1)
    }

    project()
  }

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

    repeat(ENV.maxImagesPerFrame * 2) {
      focus.current.position = size * 2
      focus.current.render(g)
      focus.inc()
    }

    wipeScreen(g)
  }

  private fun drawPage(painter: Operation<Graphics2D>? = null) {
    val drawTime = measureTimeMillis {
      (bufferStrategy.drawGraphics as Graphics2D).use {
        wipeScreen(it)
        painter?.invoke(it)
      }
      bufferStrategy.show()
    }

    log.info("Spent $drawTime ms drawing frame #$frameCount")
  }

  ///////////////////////////////////////
  // Draw Logic
  ///////////////////////////////////////
  // Order of operations:
  // - The index is modified and project() is called
  // - selectImages() performs lookahead operations to collect the images to be displayed
  // - render() draws all geometry to a screen after the field is updated
  // - 10ms delay to allow UI thread to kick in
  // - preRender() immediately draws the optimistic next images to get them cached on the UI thread.
  // - updateCaching() which re-evaluates the caching states of buffered images after rendering
  ///////////////////////////////////////

  private fun project() {
    val pages = selectImages()
    geometry = size.fitCentered(pages).plus(marginPanel)
  }

  private fun selectImages(): List<CachedImage> {
    val pages: MutableList<CachedImage> = mutableListOf()
    var margin = size.width
    var focus = index.copy

    do {
      margin -= focus.current.width
      if (margin < 0) break
      pages.add(focus.current)
      focus = focus + 1
    } while (
      ENV.paneled &&                     // Panelling is enabled
      focus.primary == index.primary &&  // We are still in the current Catalog
      pages.size < ENV.maxImagesPerFrame // Honor upper bound
    )

    return pages
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
    }

    frameCount++
    CachedImage.trimCache()
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
    if (index.maxPrimary == 0) quit(0)
    index.primary = target.coerceAtMost(index.maxPrimary - 1)
    project()
  }

  ///////////////////////////////////////
  // File Helpers
  ///////////////////////////////////////


  private fun modifyCatalogFolder(
    target: Int,
    jumpTo: Int = target,
    operation: Extension<File>
  ) {
    drawPage()
    val targetFile = library[target].file

    ::index.invalidate(this)
    library = library.filter { !it.path.startsWith(targetFile.absolutePath) }
    geometry = listOf()

    targetFile.operation()
    System.gc()
    softJump(jumpTo)
  }

  fun deleteCurrentDirectory() {
    modifyCatalogFolder(index.primary) {
      log.warning("Deleting Folder: $absolutePath")
      deleteRecursively()
    }
  }

  fun archiveCurrentDirectory() {
    modifyCatalogFolder(index.primary) {
      val newPath = File("${ENV.archive}\\$name").toPath()
      log.warning("Moving Folder: $absolutePath --> $newPath")
      if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
        log.severe("Target Path already exists! No action taken!")
      else
        Files.move(toPath(), newPath)
    }
  }
}
