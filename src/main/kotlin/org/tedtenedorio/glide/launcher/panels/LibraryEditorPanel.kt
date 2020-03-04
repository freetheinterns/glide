package org.tedtenedorio.glide.launcher.panels

import kotlinx.coroutines.sync.Mutex
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.deriveFont
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.perpendicularBox
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.CleanScrollBarUI
import org.tedtenedorio.glide.launcher.components.LabelButton
import org.tedtenedorio.glide.launcher.components.LibraryEditor
import org.tedtenedorio.glide.slideshow.Library
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread

class LibraryEditorPanel : Box(BoxLayout.Y_AXIS), ActionListener {
  private val lock: Mutex = Mutex(false)
  val safeLibrary: Library?
    get() = if (::libraryEditor.isInitialized) libraryEditor.library else null

  private lateinit var libraryEditor: LibraryEditor
  private val placeholder = LabelButton {
    title = "Click to render Library"
    hoverColor = ENV.dark
    listener = this@LibraryEditorPanel
    float = true
  }
  private val scrollWindow = JScrollPane(
    VERTICAL_SCROLLBAR_NEVER,
    HORIZONTAL_SCROLLBAR_NEVER
  ).also {
    it.minimumSize = Dimension(HARD_WIDTH, HARD_WIDTH)
    it.preferredSize = Dimension(HARD_WIDTH, Launcher.HARD_HEIGHT)
    it.maximumSize = Dimension(HARD_WIDTH, Launcher.HARD_HEIGHT)

    it.setViewportView(placeholder)
    it.background = ENV.dark
    it.verticalScrollBar.unitIncrement = 10
    it.verticalScrollBar.background = ENV.dark
    it.verticalScrollBar.ui = CleanScrollBarUI()
  }

  init {
    isOpaque = true
    border = EmptyBorder(0, 0, 0, 0)

    preferredSize = Dimension(TabPanel.HARD_WIDTH, Launcher.HARD_HEIGHT)
    minimumSize = Dimension(TabPanel.HARD_WIDTH, Launcher.HARD_HEIGHT)
    maximumSize = Dimension(TabPanel.HARD_WIDTH, Launcher.HARD_HEIGHT)
    background = ENV.background

    perpendicularBox {
      spring()
      add(JLabel("Slideshow Library").deriveFont(15).apply {
        foreground = ENV.lightForeground
        alignmentY = Component.TOP_ALIGNMENT
      })
      spring()
    }

    spring()
    gap(50)
    perpendicularBox {
      spring()
      add(scrollWindow)
      spring()
    }
    gap(50)
    spring()

    actionPerformed(null)
    isVisible = true
  }

  override fun actionPerformed(e: ActionEvent?) {
    if (!lock.tryLock()) {
      log.info("Not Reloading Library")
      return
    }
    log.info("Reloading Library...")

    placeholder.label?.text = "Loading Library..."
    scrollWindow.setViewportView(placeholder)
    thread(name = "LibraryEditorPanel-loading-library") {
      try {
        libraryEditor = LibraryEditor()
        scrollWindow.setViewportView(libraryEditor)
        scrollWindow.verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
        revalidate()
        repaint()
      } catch (exc: Exception) {
        placeholder.label?.text = """
          Failed to load Library from ${ENV.root}
          Exception: $exc
          Click to render Library
        """.trimIndent()
        scrollWindow.setViewportView(placeholder)
        scrollWindow.verticalScrollBarPolicy = VERTICAL_SCROLLBAR_NEVER
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