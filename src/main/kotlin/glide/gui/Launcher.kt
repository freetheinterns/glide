package glide.gui

import glide.slideshow.CachedImage
import glide.slideshow.EventHandler
import glide.slideshow.Projector
import glide.storage.ENV
import glide.utils.extensions.glue
import glide.utils.extensions.logger
import glide.utils.extensions.sizeTo
import glide.utils.extensions.update
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

class Launcher : JFrame("Projector: Settings"), ActionListener {
  companion object {
    private const val HARD_HEIGHT = 800

    const val LEFT_TO_RIGHT_TEXT = ":LtR (JA)"
    const val RIGHT_TO_LEFT_TEXT = ":RtL (EN)"
  }

  private val saveTab = LabelButton("Save Settings", this, defaultBackground = ENV.darkHighlight)
  private val launchTab = LabelButton("Launch", this, defaultBackground = ENV.darkHighlight)
  private val closeWindow = LabelButton(
    "X",
    ActionListener { System.exit(0) },
    defaultBackground = UIManager.getColor("Panel.background"),
    defaultSelected = Color.RED,
    width = 57,
    height = 39
  )

  private val fileOptionsTab = FileOptionsTabPanel(HARD_HEIGHT - Button.HARD_HEIGHT, this)
  private val displayOptionsTab = DisplayOptionsTabPanel(HARD_HEIGHT - Button.HARD_HEIGHT, this)
  private val advancedOptionsTab = AdvancedOptionsTabPanel(HARD_HEIGHT - Button.HARD_HEIGHT, this)

  private val cardLayout = CardLayout()
  private val cards = JPanel(cardLayout)
  private val selector = JPanel()
  private val tabOptions = arrayOf(
    fileOptionsTab,
    displayOptionsTab,
    advancedOptionsTab
  )

  private val dragListener = FrameDragListener(this)

  init {
    ENV.launcher = this
    ENV.scope = "Launcher"
    defaultCloseOperation = EXIT_ON_CLOSE
    isUndecorated = true
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH + LabelButton.HARD_WIDTH + 6, HARD_HEIGHT)
    layout = BorderLayout()

    // Set up frame listeners
    addMouseMotionListener(dragListener)
    addMouseListener(dragListener)

    val selectorLayout = SpringLayout()
    selector.layout = selectorLayout
    selector.background = ENV.dark
    selector.sizeTo(LabelButton.HARD_WIDTH, HARD_HEIGHT)
    selector.add(fileOptionsTab.label)
    selector.add(displayOptionsTab.label)
    selector.add(advancedOptionsTab.label)

    selectorLayout.glue(NORTH, fileOptionsTab.label, selector)
    selectorLayout.putConstraint(NORTH, displayOptionsTab.label, 0, SOUTH, fileOptionsTab.label)
    selectorLayout.putConstraint(NORTH, advancedOptionsTab.label, 0, SOUTH, displayOptionsTab.label)
    selectorLayout.glue(WEST, fileOptionsTab.label, selector)
    selectorLayout.glue(WEST, displayOptionsTab.label, selector)
    selectorLayout.glue(WEST, advancedOptionsTab.label, selector)

    // Float action button tabs on the bottom
    selector.add(saveTab)
    selector.add(launchTab)
    selectorLayout.glue(SOUTH, launchTab, selector)
    selectorLayout.putConstraint(SOUTH, saveTab, 0, NORTH, launchTab)
    selectorLayout.glue(WEST, launchTab, selector)
    selectorLayout.glue(WEST, saveTab, selector)

    cards.sizeTo(TabPanel.HARD_WIDTH, HARD_HEIGHT)
    cards.add(fileOptionsTab, fileOptionsTab.label.title)
    cards.add(displayOptionsTab, displayOptionsTab.label.title)
    cards.add(advancedOptionsTab, advancedOptionsTab.label.title)

    add(selector, BorderLayout.WEST)
    add(cards, BorderLayout.CENTER)

    isResizable = false
    isFocusable = true
    isVisible = true
    requestFocusInWindow()
    EventHandler.register()
  }

  override fun actionPerformed(e: ActionEvent) = when (e.source) {
    saveTab                  -> save()
    launchTab                -> launch()
    closeWindow              -> System.exit(0)

    fileOptionsTab.label     -> changeCard(fileOptionsTab)
    displayOptionsTab.label  -> changeCard(displayOptionsTab)
    advancedOptionsTab.label -> changeCard(advancedOptionsTab)

    else                     -> logger.warning("Miss for ${e.source::class}: ${e.source}")
  }

  private fun save() {
    ENV.update {
      archive = fileOptionsTab.archive.banner.text
      root = fileOptionsTab.root.banner.text

      ordering = fileOptionsTab.ordering.selectedItem as String
      fontName = displayOptionsTab.fontName.selectedItem as String
      scaling = CachedImage.SCALING_REMAP[displayOptionsTab.scaling.selectedItem]!!

      speed = advancedOptionsTab.speed.value
      debounce = advancedOptionsTab.debounce.value.toLong()
      imageBufferCapacity = advancedOptionsTab.imageBufferCapacity.value
      intraPlaylistVision = advancedOptionsTab.intraPlaylistVision.value

      direction = displayOptionsTab.directionGroup.selectedItem == LEFT_TO_RIGHT_TEXT
      paneled = displayOptionsTab.paneledInput.isSelected
      showFooterFileNumber = displayOptionsTab.showFooterFileNumberInput.isSelected
      showMarginFileCount = displayOptionsTab.showMarginFileCountInput.isSelected
      showMarginFileName = displayOptionsTab.showMarginFileNameInput.isSelected
      showMarginFolderCount = displayOptionsTab.showMarginFolderCountInput.isSelected
      showMarginFolderName = displayOptionsTab.showMarginFolderNameInput.isSelected
      verbose = advancedOptionsTab.verboseInput.isSelected
    }
  }

  fun launch() {
    save()
    ENV.launcher = null
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