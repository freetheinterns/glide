package org.tedtenedorio.glide.slideshow

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.Extension
import org.tedtenedorio.glide.Operation
import org.tedtenedorio.glide.extensions.catalogs
import org.tedtenedorio.glide.extensions.dimension
import org.tedtenedorio.glide.extensions.fitCentered
import org.tedtenedorio.glide.extensions.imageCount
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.times
import org.tedtenedorio.glide.extensions.use
import org.tedtenedorio.glide.launcher.panels.FullScreenFrame
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.properties.CachedProperty.Companion.invalidate
import org.tedtenedorio.glide.properties.ChangeTriggeringProperty.Companion.blindObserver
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import org.tedtenedorio.glide.slideshow.geometry.Geometry
import org.tedtenedorio.glide.slideshow.geometry.MarginPanel
import org.tedtenedorio.glide.storage.Cacheable
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.event.ActionListener
import java.io.File
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

  var geometry by blindObserver(listOf<Geometry>(), ::render)
  var library: List<Catalog> by cache { File(ENV.root).catalogs }
  val index: ImageIndex by cache { ImageIndex(library) }
  var frameCount: Int = 0
  val timer = Timer(ENV.speed, ActionListener { next() })

  private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
  private val marginPanel = MarginPanel(this)

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

    // IMPORTANT!! Register the screen globally
    singleton = this

    // Short-circuit if playlist is empty or if full screen is not possible
    if (library.sumBy(Catalog::size) == 0) {
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

    repeat(ENV.maxImagesPerFrame) {
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
  // - Cacheable.manageGlobalCache() which re-evaluates the caching states of cached images
  ///////////////////////////////////////

  fun project() {
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
    Cacheable.manageGlobalCache()
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

    ::index.invalidate(this)
    library = library.filter { !it.path.startsWith(targetFile.absolutePath) }
    geometry = listOf()

    targetFile.operation()
    System.gc()
    if (index.maxPrimary == 0) quit(0)
    index.primary = jumpTo.coerceAtMost(index.maxPrimary - 1)
  }
}