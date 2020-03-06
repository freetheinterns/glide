package org.tedtenedorio.glide.launcher

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.extensions.box
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.extensions.warn
import org.tedtenedorio.glide.launcher.components.LabelButton
import org.tedtenedorio.glide.launcher.panels.AdvancedOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.DisplayOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.FileOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.LibraryEditorPanel
import org.tedtenedorio.glide.launcher.panels.TabPanel
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.listeners.FrameDragListener
import org.tedtenedorio.glide.slideshow.Library
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Persist.save
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.JFrame
import javax.swing.JPanel

class Launcher : JFrame("Projector: Custom Comic Slideshows"), ActionListener {
  private val saveTab = LabelButton {
    title = "Save Settings"
    listener = this@Launcher
    background = ENV.darkHighlight
    color = ENV.darkHighlight
    textColor = ENV.foreground
  }
  private val launchTab = LabelButton {
    title = "Launch"
    listener = this@Launcher
    background = ENV.darkHighlight
    color = ENV.darkHighlight
    textColor = ENV.foreground
  }

  private val fileOptionsTab = FileOptionsTabPanel(this)
  private val displayOptionsTab = DisplayOptionsTabPanel(this)
  private val advancedOptionsTab = AdvancedOptionsTabPanel(this)

  val libraryEditorPanel = LibraryEditorPanel()

  private val selector: Box
  private val cards: JPanel

  private val tabOptions = arrayOf(
    fileOptionsTab,
    displayOptionsTab,
    advancedOptionsTab
  )

  private val dragListener = FrameDragListener { location = it }

  init {
    defaultCloseOperation = EXIT_ON_CLOSE
    isUndecorated = true
    isResizable = false
    isFocusable = true
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH * 2 + LabelButton.HARD_WIDTH + 6, HARD_HEIGHT)

    addMouseMotionListener(dragListener)
    addMouseListener(dragListener)

    selector = box {
      preferredSize = Dimension(LabelButton.HARD_WIDTH, HARD_HEIGHT)
      minimumSize = Dimension(LabelButton.HARD_WIDTH, HARD_HEIGHT)
      maximumSize = Dimension(LabelButton.HARD_WIDTH, HARD_HEIGHT)
      background = ENV.dark

      add(fileOptionsTab.label)
      add(displayOptionsTab.label)
      add(advancedOptionsTab.label)
      spring()
      // add(saveTab)
      add(launchTab)
    }

    cards = JPanel(CardLayout()).apply {
      preferredSize = Dimension(TabPanel.HARD_WIDTH, HARD_HEIGHT)
      minimumSize = Dimension(TabPanel.HARD_WIDTH, HARD_HEIGHT)
      maximumSize = Dimension(TabPanel.HARD_WIDTH, HARD_HEIGHT)
      add(fileOptionsTab, fileOptionsTab.label.title)
      add(displayOptionsTab, displayOptionsTab.label.title)
      add(advancedOptionsTab, advancedOptionsTab.label.title)
    }

    add(libraryEditorPanel, BorderLayout.WEST)
    add(selector, BorderLayout.CENTER)
    add(cards, BorderLayout.EAST)

    isVisible = true
    requestFocusInWindow()
    EventHandler.target = this
  }

  override fun actionPerformed(e: ActionEvent) = when (e.source) {
    saveTab -> save()
    launchTab -> launchProjector()

    fileOptionsTab.label -> changeCard(fileOptionsTab)
    displayOptionsTab.label -> changeCard(displayOptionsTab)
    advancedOptionsTab.label -> changeCard(advancedOptionsTab)

    else -> log.warn { "Miss for ${e.source::class.simpleName}: ${e.source}" }
  }

  fun save() {
    val oldSettings = ENV.copy()
    ENV.archive = fileOptionsTab.archive.banner.text
    ENV.root = fileOptionsTab.root.banner.text
    ENV.ordering = fileOptionsTab.ordering.selectedItem as FolderSortStrategy
    ENV.fontName = displayOptionsTab.fontName.selectedItem as String
    ENV.speed = advancedOptionsTab.speed.value
    ENV.debounce = advancedOptionsTab.debounce.value.toLong()
    ENV.maxImagesPerFrame = advancedOptionsTab.imageBuffer.value
    ENV.direction = displayOptionsTab.directionGroup.selectedItem == LEFT_TO_RIGHT_TEXT
    ENV.paneled = displayOptionsTab.paneledInput.isSelected
    ENV.showFooterFileNumber = displayOptionsTab.showFooterFileNumberInput.isSelected
    ENV.showMarginFileCount = displayOptionsTab.showMarginFileCountInput.isSelected
    ENV.showMarginFileName = displayOptionsTab.showMarginFileNameInput.isSelected
    ENV.showMarginFolderCount = displayOptionsTab.showMarginFolderCountInput.isSelected
    ENV.showMarginFolderName = displayOptionsTab.showMarginFolderNameInput.isSelected
    ENV.save()
    if (ENV.archive != oldSettings.archive || ENV.root != oldSettings.root || ENV.ordering != oldSettings.ordering)
      libraryEditorPanel.actionPerformed(null)
  }

  fun launchProjector() {
    save()
    Projector(libraryEditorPanel.safeLibrary ?: Library(ENV.root))
    dispose()
  }

  private fun changeCard(target: TabPanel) {
    (cards.layout as CardLayout).show(cards, target.label.title)
    tabOptions.forEach { it.highlighted = (it == target) }
  }

  fun nextCard() {
    var nextIndex = tabOptions.indexOfFirst { it.highlighted } + 1
    if (nextIndex >= tabOptions.size) nextIndex = 0
    changeCard(tabOptions[nextIndex])
  }

  companion object {
    private val log by logger()
    const val HARD_HEIGHT = 900
    const val LEFT_TO_RIGHT_TEXT = ":LtR (JA)"
    const val RIGHT_TO_LEFT_TEXT = ":RtL (EN)"
  }
}