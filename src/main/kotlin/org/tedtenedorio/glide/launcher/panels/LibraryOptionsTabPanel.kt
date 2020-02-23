package org.tedtenedorio.glide.launcher.panels

import kotlinx.coroutines.sync.Mutex
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.perpendicularBox
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.LabelButton
import org.tedtenedorio.glide.launcher.components.LibraryEditor
import org.tedtenedorio.glide.slideshow.Library
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
import kotlin.concurrent.thread

class LibraryOptionsTabPanel(
  launcher: Launcher
) : TabPanel("Library Options", launcher), ActionListener {
  val safeLibrary: Library?
    get() = if (::libraryEditor.isInitialized) libraryEditor.library else null

  lateinit var libraryEditor: LibraryEditor
  private val placeholder = LabelButton {
    title = "Click to render Library"
    hoverColor = ENV.dark
    listener = this@LibraryOptionsTabPanel
    float = true
  }
  private val window = JScrollPane(
    VERTICAL_SCROLLBAR_NEVER,
    HORIZONTAL_SCROLLBAR_NEVER
  ).also {
    it.minimumSize = Dimension(HARD_WIDTH, HARD_WIDTH)
    it.preferredSize = Dimension(HARD_WIDTH, maximumSize.height)
    it.maximumSize = Dimension(HARD_WIDTH, maximumSize.height)
    it.setViewportView(placeholder)
    it.background = ENV.dark
    it.verticalScrollBar.unitIncrement = 10
  }

  init {
    gap(50)
    perpendicularBox {
      spring()
      add(window)
      spring()
    }
    gap(50)
  }

  private val lock: Mutex = Mutex(false)

  override fun actionPerformed(e: ActionEvent?) {
    if (!lock.tryLock()) {
      log.info("Not Reloading Library")
      return
    }
    log.info("Reloading Library...")

    placeholder.label?.text = "Loading Library..."
    window.setViewportView(placeholder)
    thread(
      isDaemon = true,
      name = "LibraryOptionsTabPanel-loading-library"
    ) {
      try {
        libraryEditor = LibraryEditor()
        window.setViewportView(libraryEditor)
        window.verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
        revalidate()
        repaint()
      } catch (exc: Exception) {
        placeholder.label?.text = """
          Failed to load Library from ${ENV.root}
          Exception: $exc
          Click to render Library
        """.trimIndent()
        window.setViewportView(placeholder)
        window.verticalScrollBarPolicy = VERTICAL_SCROLLBAR_NEVER
        exc.printStackTrace()
      } finally {
        lock.unlock()
        log.info("Done Reloading Library")
      }
    }
  }

  companion object {
    const val HARD_WIDTH = TabPanel.HARD_WIDTH - 100
    private val log by logger()
  }
}