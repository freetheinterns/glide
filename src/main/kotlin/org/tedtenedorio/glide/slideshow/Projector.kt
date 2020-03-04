package org.tedtenedorio.glide.slideshow

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.tedtenedorio.glide.BACKGROUND_DISPATCHER
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.Extension
import org.tedtenedorio.glide.Operation
import org.tedtenedorio.glide.extensions.FRAME_RENDER_PRIORITY
import org.tedtenedorio.glide.extensions.PROJECTOR_WINDOW_SIZE
import org.tedtenedorio.glide.extensions.debug
import org.tedtenedorio.glide.extensions.dimension
import org.tedtenedorio.glide.extensions.error
import org.tedtenedorio.glide.extensions.fitCentered
import org.tedtenedorio.glide.extensions.imageCount
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.times
import org.tedtenedorio.glide.extensions.trace
import org.tedtenedorio.glide.extensions.use
import org.tedtenedorio.glide.launcher.components.FullScreenFrame
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.properties.CachedProperty.Companion.invalidate
import org.tedtenedorio.glide.properties.ChangeTriggeringProperty.Companion.blindObserver
import org.tedtenedorio.glide.properties.lazyAwait
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import org.tedtenedorio.glide.slideshow.geometry.Geometry
import org.tedtenedorio.glide.slideshow.geometry.MarginPanel
import org.tedtenedorio.glide.storage.Cacheable.Companion.manageGlobalCache
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.event.ActionListener
import java.io.File
import javax.swing.Timer
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

class Projector(
  val library: Library
) : FullScreenFrame() {
  var geometry by blindObserver(listOf<Geometry>(), ::render)
  val index: Library.Index by cache { library.Index() }
  val timer = Timer(ENV.speed, ActionListener {
    EventHandler.handleEvent(EventHandler.PROJECTOR_BINDINGS.pageForward.first())
  })

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)
  private var backgroundTasks = mutableListOf<Job>()

  init {
    if (!device.isFullScreenSupported)
      throw IllegalArgumentException("Full-screen modes not yet supported on device")

    // Set up Listeners
    defaultCloseOperation = DO_NOTHING_ON_CLOSE
    addMouseListener(EventHandler)
    timer.initialDelay = ENV.speed

    // Set up JFrame
    ignoreRepaint = true
    isResizable = false
    isUndecorated = true
    focusTraversalKeysEnabled = false

    // Set JFrame to full screen
    device.fullScreenWindow = this
    device.displayMode = ENV.displayMode
    size = device.displayMode.dimension

    // Use a dual imageBufferCapacity strategy for Graphics control
    createBufferStrategy(2)

    // Draw initial black background
    drawPage()
    PROJECTOR_WINDOW_SIZE = size

    // IMPORTANT!! Register the screen with the event manager
    EventHandler.target = this

    if (library.isEmpty) {
      log.error { "No images found to display!" }
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

  private suspend fun preRender() {
    val focus = index + geometry.imageCount
    val realPriority = FRAME_RENDER_PRIORITY

    try {
      FRAME_RENDER_PRIORITY -= 2

      repeat(ENV.maxImagesPerFrame) {
        if (!coroutineContext.isActive) return
        focus.current.position = size * 2
        focus += 1
      }
    } finally {
      FRAME_RENDER_PRIORITY = realPriority
    }

    manageGlobalCache()
  }

  private fun drawPage(painter: Operation<Graphics2D>? = null) {
    FRAME_RENDER_PRIORITY++

    val drawTime = measureTimeMillis {
      (bufferStrategy.drawGraphics as Graphics2D).use {
        wipeScreen(it)
        painter?.invoke(it)
      }
      bufferStrategy.show()
    }

    log.trace { "Spent $drawTime ms drawing frame #$FRAME_RENDER_PRIORITY" }
  }

  ///////////////////////////////////////
  // Draw Logic
  ///////////////////////////////////////
  // Order of operations:
  // - The index is modified and project() is called
  // - selectImages() performs lookahead operations to collect the images to be displayed
  // - render() draws all geometry to a screen after the field is updated
  // - 10ms delay to allow UI thread to kick in
  // - preRender() immediately tries to load the next frame into memory
  // - Cacheable.manageGlobalCache() which re-evaluates the caching states of cached images
  ///////////////////////////////////////

  fun project(): Unit = runBlocking {
    val pages by lazyAwait { selectImages() }
    backgroundTasks.forEach { it.cancel() }
    backgroundTasks.clear()
    geometry = size.fitCentered(pages).plus(marginPanel)
  }

  private fun selectImages(): List<CachedImage> {
    val pages: MutableList<CachedImage> = mutableListOf()
    var margin = size.width
    var focus = index.copy
    log.debug { "Projecting ${index.current}" }

    do {
      margin -= focus.current.width
      if (margin < 0) break
      pages.add(focus.current)
      focus++
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
    runBlocking { delay(10) }
    backgroundTasks.plusAssign(GlobalScope.launch(BACKGROUND_DISPATCHER) { preRender() })
  }

  ///////////////////////////////////////
  // Image Iterator Helpers
  ///////////////////////////////////////

  fun next() {
    index += geometry.imageCount
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
  }

  ///////////////////////////////////////
  // File Helpers
  ///////////////////////////////////////


  fun modifyCatalogFolder(
    target: Int,
    jumpTo: Int = target,
    operation: Extension<File>
  ) {
    drawPage()
    val targetFile = library[target].file

    library.filter { !it.path.startsWith(targetFile.absolutePath) }
    ::index.invalidate(this)
    geometry = listOf()

    targetFile.operation()
    System.gc()
    if (index.maxPrimary == 0) quit(0)
    index.primary = jumpTo.coerceAtMost(index.maxPrimary - 1)
  }

  companion object {
    private val log by logger()
  }
}
