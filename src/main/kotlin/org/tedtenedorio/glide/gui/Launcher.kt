package org.tedtenedorio.glide.gui

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.glue
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.sizeTo
import org.tedtenedorio.glide.gui.components.LabelButton
import org.tedtenedorio.glide.gui.listeners.FrameDragListener
import org.tedtenedorio.glide.gui.panels.AdvancedOptionsTabPanel
import org.tedtenedorio.glide.gui.panels.DisplayOptionsTabPanel
import org.tedtenedorio.glide.gui.panels.FileOptionsTabPanel
import org.tedtenedorio.glide.gui.panels.TabPanel
import org.tedtenedorio.glide.slideshow.Projector
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
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
    var singleton: Launcher? = null
    private val log by logger()
    private const val HARD_HEIGHT = 900

    const val LEFT_TO_RIGHT_TEXT = ":LtR (JA)"
    const val RIGHT_TO_LEFT_TEXT = ":RtL (EN)"
  }

  private val saveTab =
    LabelButton("Save Settings", this, defaultBackground = ENV.darkHighlight, foreground = ENV.foreground)
  private val launchTab =
    LabelButton("Launch", this, defaultBackground = ENV.darkHighlight, foreground = ENV.foreground)
  private val closeWindow = LabelButton(
    "X",
    ActionListener { exitProcess(0) },
    defaultBackground = UIManager.getColor("Panel.background"),
    defaultSelected = Color.RED,
    width = 57,
    height = 39
  )

  private val fileOptionsTab =
    FileOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)
  private val displayOptionsTab =
    DisplayOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)
  private val advancedOptionsTab =
    AdvancedOptionsTabPanel(HARD_HEIGHT - LabelButton.HARD_HEIGHT, this)

  private val cardLayout = CardLayout()
  private val cards = JPanel(cardLayout).apply {
    sizeTo(TabPanel.HARD_WIDTH, HARD_HEIGHT)
    add(fileOptionsTab, fileOptionsTab.label.title)
    add(displayOptionsTab, displayOptionsTab.label.title)
    add(advancedOptionsTab, advancedOptionsTab.label.title)
  }

  private val selectorLayout = SpringLayout()
  private val selector = JPanel().apply constructSelector@{
    layout = selectorLayout.also {
      it.glue(NORTH, fileOptionsTab.label, this@constructSelector)
      it.putConstraint(NORTH, displayOptionsTab.label, 0, SOUTH, fileOptionsTab.label)
      it.putConstraint(NORTH, advancedOptionsTab.label, 0, SOUTH, displayOptionsTab.label)
      it.glue(WEST, fileOptionsTab.label, this@constructSelector)
      it.glue(WEST, displayOptionsTab.label, this@constructSelector)
      it.glue(WEST, advancedOptionsTab.label, this@constructSelector)
      it.glue(SOUTH, launchTab, this@constructSelector)
      // Float action button tabs on the bottom
      it.putConstraint(SOUTH, saveTab, 0, NORTH, launchTab)
      it.glue(WEST, launchTab, this@constructSelector)
      it.glue(WEST, saveTab, this@constructSelector)
    }
    background = ENV.dark
    sizeTo(LabelButton.HARD_WIDTH, HARD_HEIGHT)
    add(fileOptionsTab.label)
    add(displayOptionsTab.label)
    add(advancedOptionsTab.label)
    add(saveTab)
    add(launchTab)
  }
  private val tabOptions = arrayOf(
    fileOptionsTab,
    displayOptionsTab,
    advancedOptionsTab
  )

  private val dragListener = FrameDragListener { location = it }.also {
    addMouseMotionListener(it)
    addMouseListener(it)
  }

  init {
    singleton = this
    defaultCloseOperation = EXIT_ON_CLOSE
    isUndecorated = true
    isResizable = false
    isFocusable = true
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH + LabelButton.HARD_WIDTH + 6, HARD_HEIGHT)
    layout = BorderLayout()

    add(selector, BorderLayout.WEST)
    add(cards, BorderLayout.CENTER)

    isVisible = true
    requestFocusInWindow()
  }

  override fun actionPerformed(e: ActionEvent) = when (e.source) {
    saveTab                  -> save()
    launchTab                -> launchProjector()
    closeWindow              -> exitProcess(0)

    fileOptionsTab.label     -> changeCard(fileOptionsTab)
    displayOptionsTab.label  -> changeCard(displayOptionsTab)
    advancedOptionsTab.label -> changeCard(advancedOptionsTab)

    else                     -> log.warning("Miss for ${e.source::class.simpleName}: ${e.source}")
  }

  private fun save() {
    ENV.archive = fileOptionsTab.archive.banner.text
    ENV.root = fileOptionsTab.root.banner.text
    ENV.ordering =
      fileOptionsTab.ordering.selectedItem as _root_ide_package_.org.tedtenedorio.glide.enums.FolderSortStrategy
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
    singleton = null
    Projector()
    dispose()
  }

  private fun changeCard(target: TabPanel) {
    cardLayout.show(cards, target.label.title)
    tabOptions.forEach { it.highlighted = (it == target) }
  }

  fun nextCard() {
    var nextIndex = tabOptions.indexOfFirst { it.highlighted } + 1
    if (nextIndex >= tabOptions.size) nextIndex = 0
    changeCard(tabOptions[nextIndex])
  }
}