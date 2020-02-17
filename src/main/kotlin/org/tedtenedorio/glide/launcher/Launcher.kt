package org.tedtenedorio.glide.launcher

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.extensions.glue
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.sizeTo
import org.tedtenedorio.glide.launcher.components.LabelButton
import org.tedtenedorio.glide.launcher.panels.AdvancedOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.DisplayOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.FileOptionsTabPanel
import org.tedtenedorio.glide.launcher.panels.TabPanel
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.listeners.FrameDragListener
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Persist.save
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color.RED
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SpringLayout
import javax.swing.SpringLayout.NORTH
import javax.swing.SpringLayout.SOUTH
import javax.swing.SpringLayout.WEST
import javax.swing.UIManager
import kotlin.system.exitProcess

class Launcher : JFrame("Projector: Settings"), ActionListener {
  companion object {
    private val log by logger()
    private const val HARD_HEIGHT = 900

    const val LEFT_TO_RIGHT_TEXT = ":LtR (JA)"
    const val RIGHT_TO_LEFT_TEXT = ":RtL (EN)"
  }

  private val saveTab = LabelButton {
    title = "Save Settings"
    listener = this@Launcher
    background = ENV.darkHighlight
    foreground = ENV.foreground
  }
  private val launchTab = LabelButton {
    title = "Launch"
    listener = this@Launcher
    background = ENV.darkHighlight
    foreground = ENV.foreground
  }
  private val closeWindow = LabelButton {
    title = "X"
    listener = ActionListener { exitProcess(0) }
    background = UIManager.getColor("Panel.background")
    hoverColor = RED
    size = Dimension(57, 39)
  }

  private val fileOptionsTab =
    FileOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)
  private val displayOptionsTab =
    DisplayOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)
  private val advancedOptionsTab =
    AdvancedOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)

  private val cardLayout = CardLayout()
  private val cards = JPanel(cardLayout).apply {
    sizeTo(TabPanel.HARD_WIDTH, HARD_HEIGHT)
    add(fileOptionsTab, fileOptionsTab.label.settings.title)
    add(displayOptionsTab, displayOptionsTab.label.settings.title)
    add(advancedOptionsTab, advancedOptionsTab.label.settings.title)
  }

  private val selector = JPanel().also { builder ->
    builder.layout = SpringLayout().also {
      it.glue(NORTH, fileOptionsTab.label, builder)
      it.putConstraint(NORTH, displayOptionsTab.label, 0, SOUTH, fileOptionsTab.label)
      it.putConstraint(NORTH, advancedOptionsTab.label, 0, SOUTH, displayOptionsTab.label)
      it.glue(WEST, fileOptionsTab.label, builder)
      it.glue(WEST, displayOptionsTab.label, builder)
      it.glue(WEST, advancedOptionsTab.label, builder)
      it.glue(SOUTH, launchTab, builder)
      // Float action button tabs on the bottom
      it.putConstraint(SOUTH, saveTab, 0, NORTH, launchTab)
      it.glue(WEST, launchTab, builder)
      it.glue(WEST, saveTab, builder)
    }
    builder.background = ENV.dark
    builder.sizeTo(LabelButton.HARD_WIDTH, HARD_HEIGHT)
    builder.add(fileOptionsTab.label)
    builder.add(displayOptionsTab.label)
    builder.add(advancedOptionsTab.label)
    builder.add(saveTab)
    builder.add(launchTab)
  }
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
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH + LabelButton.HARD_WIDTH + 6, HARD_HEIGHT)
    layout = BorderLayout()

    addMouseMotionListener(dragListener)
    addMouseListener(dragListener)

    add(selector, BorderLayout.WEST)
    add(cards, BorderLayout.CENTER)

    isVisible = true
    requestFocusInWindow()
    EventHandler.target = this
  }

  override fun actionPerformed(e: ActionEvent) = when (e.source) {
    saveTab -> save()
    launchTab -> launchProjector()
    closeWindow -> exitProcess(0)

    fileOptionsTab.label -> changeCard(fileOptionsTab)
    displayOptionsTab.label -> changeCard(displayOptionsTab)
    advancedOptionsTab.label -> changeCard(advancedOptionsTab)

    else -> log.warning("Miss for ${e.source::class.simpleName}: ${e.source}")
  }

  private fun save() {
    ENV.archive = fileOptionsTab.archive.banner.text
    ENV.root = fileOptionsTab.root.banner.text
    ENV.ordering = fileOptionsTab.ordering.selectedItem as FolderSortStrategy
    ENV.fontName = displayOptionsTab.fontName.selectedItem as String
    ENV.speed = advancedOptionsTab.speed.value
    ENV.debounce = advancedOptionsTab.debounce.value.toLong()
    ENV.maxImagesPerFrame = advancedOptionsTab.maxImagesPerFrame.value
    ENV.direction = displayOptionsTab.directionGroup.selectedItem == LEFT_TO_RIGHT_TEXT
    ENV.paneled = displayOptionsTab.paneledInput.isSelected
    ENV.showFooterFileNumber = displayOptionsTab.showFooterFileNumberInput.isSelected
    ENV.showMarginFileCount = displayOptionsTab.showMarginFileCountInput.isSelected
    ENV.showMarginFileName = displayOptionsTab.showMarginFileNameInput.isSelected
    ENV.showMarginFolderCount = displayOptionsTab.showMarginFolderCountInput.isSelected
    ENV.showMarginFolderName = displayOptionsTab.showMarginFolderNameInput.isSelected
    ENV.verbose = advancedOptionsTab.verboseInput.isSelected
    ENV.save()
  }

  fun launchProjector() {
    save()
    Projector()
    dispose()
  }

  private fun changeCard(target: TabPanel) {
    cardLayout.show(cards, target.label.settings.title)
    tabOptions.forEach { it.highlighted = (it == target) }
  }

  fun nextCard() {
    var nextIndex = tabOptions.indexOfFirst { it.highlighted } + 1
    if (nextIndex >= tabOptions.size) nextIndex = 0
    changeCard(tabOptions[nextIndex])
  }
}