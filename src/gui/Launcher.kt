package gui

import slideshow.CachedImage
import slideshow.Projector
import storage.ENV
import utils.extensions.glue
import utils.extensions.sizeTo
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SpringLayout
import javax.swing.SpringLayout.NORTH
import javax.swing.SpringLayout.SOUTH
import javax.swing.SpringLayout.WEST
import javax.swing.UIManager

class Launcher : JFrame("Projector: Settings"), ActionListener, KeyListener {
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

  private val cards = JPanel(CardLayout())
  private val selector = JPanel()

  private val dragListener = FrameDragListener(this)

  init {
    defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    isUndecorated = true
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH + LabelButton.HARD_WIDTH + 6, HARD_HEIGHT)
    layout = BorderLayout()

    // Set up frame listeners
    addMouseMotionListener(dragListener)
    addMouseListener(dragListener)
    addKeyListener(this)

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
    isVisible = true
  }

  override fun actionPerformed(e: ActionEvent) = processSource(e.source)
  override fun keyPressed(e: KeyEvent) {}
  override fun keyReleased(e: KeyEvent) {}
  override fun keyTyped(e: KeyEvent) {}

  private fun processSource(obj: Any) = when (obj) {
    saveTab                  -> save()
    launchTab                -> launch()
    closeWindow              -> System.exit(0)

    fileOptionsTab.label     -> (cards.layout as CardLayout).show(cards, fileOptionsTab.label.title)
    displayOptionsTab.label  -> (cards.layout as CardLayout).show(cards, displayOptionsTab.label.title)
    advancedOptionsTab.label -> (cards.layout as CardLayout).show(cards, advancedOptionsTab.label.title)

    else                     -> println("Miss for ${obj::class}: $obj")
  }

  private fun save() {
    ENV.lock.block {
      ENV.archive = fileOptionsTab.archive.banner.text
      ENV.root = fileOptionsTab.root.banner.text

      ENV.ordering = fileOptionsTab.ordering.selectedItem as String
      ENV.fontName = displayOptionsTab.fontName.selectedItem as String
      ENV.scaling = CachedImage.SCALING_REMAP[displayOptionsTab.scaling.selectedItem]!!

      ENV.speed = advancedOptionsTab.speed.value
      ENV.debounce = advancedOptionsTab.debounce.value.toLong()
      ENV.imageBufferCapacity = advancedOptionsTab.imageBufferCapacity.value
      ENV.intraPlaylistVision = advancedOptionsTab.intraPlaylistVision.value

      ENV.direction = displayOptionsTab.directionGroup.selectedItem == LEFT_TO_RIGHT_TEXT
      ENV.paneled = displayOptionsTab.paneledInput.isSelected
      ENV.showFooterFileNumber = displayOptionsTab.showFooterFileNumberInput.isSelected
      ENV.showMarginFileCount = displayOptionsTab.showMarginFileCountInput.isSelected
      ENV.showMarginFileName = displayOptionsTab.showMarginFileNameInput.isSelected
      ENV.showMarginFolderCount = displayOptionsTab.showMarginFolderCountInput.isSelected
      ENV.showMarginFolderName = displayOptionsTab.showMarginFolderNameInput.isSelected
      ENV.verbose = advancedOptionsTab.verboseInput.isSelected
    }

    ENV.save()
  }

  private fun launch() {
    save()
    Projector()
    dispose()
  }
}