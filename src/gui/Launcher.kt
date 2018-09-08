package gui

import slideshow.CachedImage
import slideshow.Projector
import storage.ENV
import utils.FontNameRenderer
import utils.extensions.addGridBag
import utils.extensions.sizeTo
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JTextField
import javax.swing.SpringLayout

class Launcher : JFrame("slideshow.Projector: Settings"), ActionListener, KeyListener {
  companion object {
    private const val labelFontSize = 16
    private const val HARD_HEIGHT = 800

    private const val LEFT_TO_RIGHT_TEXT = ":LtR (JA)"
    private const val RIGHT_TO_LEFT_TEXT = ":RtL (EN)"
  }

  private val archive: JTextField
  private val root: JTextField

  private val directionGroup: JComboBox<String>
  private val fontName: JComboBox<String>
  private val ordering: JComboBox<String>
  private val scaling: JComboBox<String>

  private val debounce: JSlider
  private val imageBufferCapacity: JSlider
  private val intraPlaylistVision: JSlider
  private val speed: JSlider

  private val paneledInput: JCheckBox
  private val showFooterFileNumberInput: JCheckBox
  private val showMarginFileCountInput: JCheckBox
  private val showMarginFileNameInput: JCheckBox
  private val showMarginFolderCountInput: JCheckBox
  private val showMarginFolderNameInput: JCheckBox
  private val verboseInput: JCheckBox

  private val saveButton = Button("Save Settings", this)
  private val launchButton = Button("Launch", this)

  private val fileOptionsTab = TabPanel("Directories", HARD_HEIGHT - Button.HARD_HEIGHT, this)
  private val displayOptionsTab = TabPanel("Display", HARD_HEIGHT - Button.HARD_HEIGHT, this)
  private val advancedOptionsTab = TabPanel("Advanced", HARD_HEIGHT - Button.HARD_HEIGHT, this)

  private val cards = JPanel(CardLayout())
  private val selector = JPanel()

  init {
    defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    bounds = Rectangle(300, 200, TabPanel.HARD_WIDTH + TabLabel.HARD_WIDTH + 6, HARD_HEIGHT)
    layout = BorderLayout()

    archive = fileOptionsTab.buildTextField("Archive:", ENV.archive)
    root = fileOptionsTab.buildTextField("Home:", ENV.root)
    ordering = fileOptionsTab.buildComboBox("Folder Sort:", ENV.ORDER_ENUMS, ENV.ordering)

    scaling = displayOptionsTab.buildComboBox(
      "Scaling:",
      CachedImage.SCALING_OPTIONS.values.toTypedArray(),
      CachedImage.SCALING_OPTIONS[ENV.scaling]
    )
    fontName = displayOptionsTab.buildComboBox("Font Family:", ENV.FONT_FAMILIES, ENV.fontName)
    fontName.font = Font(ENV.fontName, Font.BOLD, labelFontSize)
    fontName.renderer = FontNameRenderer()

    paneledInput = displayOptionsTab.buildCheckBox(":Display Multiple Images", ENV.paneled)
    directionGroup = displayOptionsTab.buildComboBox(
      "Page Orientation",
      arrayOf(LEFT_TO_RIGHT_TEXT, RIGHT_TO_LEFT_TEXT),
      if (ENV.direction) LEFT_TO_RIGHT_TEXT else RIGHT_TO_LEFT_TEXT
    )
    showMarginFolderCountInput = displayOptionsTab.buildCheckBox(":Folder Count", ENV.showMarginFolderCount)
    showMarginFolderNameInput = displayOptionsTab.buildCheckBox(":Folder Name", ENV.showMarginFolderName)
    showMarginFileCountInput = displayOptionsTab.buildCheckBox(":File Count", ENV.showMarginFileCount)
    showMarginFileNameInput = displayOptionsTab.buildCheckBox(":File Name", ENV.showMarginFileName)
    showFooterFileNumberInput = displayOptionsTab.buildCheckBox(":Footer File#", ENV.showFooterFileNumber)
    verboseInput = displayOptionsTab.buildCheckBox(":verbose", ENV.verbose)

    debounce = advancedOptionsTab.buildSlider("Debounce:", 20, 520, ENV.debounce.toInt(), "(%dms)")
    speed = advancedOptionsTab.buildSlider("Slideshow Speed:", 250, 10250, ENV.speed, "(%dms)")
    intraPlaylistVision = advancedOptionsTab.buildSlider("Lookahead:", 5, 105, ENV.intraPlaylistVision)
    imageBufferCapacity = advancedOptionsTab.buildSlider("Buffered Images:", 2, 10, ENV.imageBufferCapacity)

    val selectorLayout = SpringLayout()
    selector.layout = selectorLayout
    selector.background = TabLabel.colorNormal
    selector.sizeTo(TabLabel.HARD_WIDTH, HARD_HEIGHT)
    selector.add(fileOptionsTab.label)
    selector.add(displayOptionsTab.label)
    selector.add(advancedOptionsTab.label)
    selectorLayout.putConstraint(SpringLayout.NORTH, fileOptionsTab.label, 0, SpringLayout.NORTH, selector)
    selectorLayout.putConstraint(SpringLayout.NORTH, displayOptionsTab.label, 0, SpringLayout.SOUTH, fileOptionsTab.label)
    selectorLayout.putConstraint(SpringLayout.NORTH, advancedOptionsTab.label, 0, SpringLayout.SOUTH, displayOptionsTab.label)
    selectorLayout.putConstraint(SpringLayout.WEST, fileOptionsTab.label, 0, SpringLayout.WEST, selector)
    selectorLayout.putConstraint(SpringLayout.WEST, displayOptionsTab.label, 0, SpringLayout.WEST, selector)
    selectorLayout.putConstraint(SpringLayout.WEST, advancedOptionsTab.label, 0, SpringLayout.WEST, selector)
    add(selector, BorderLayout.WEST)

    val innerPanel = JPanel(BorderLayout())
    cards.sizeTo(TabPanel.HARD_WIDTH, HARD_HEIGHT - Button.HARD_HEIGHT)
    cards.add(fileOptionsTab, fileOptionsTab.label.title)
    cards.add(displayOptionsTab, displayOptionsTab.label.title)
    cards.add(advancedOptionsTab, advancedOptionsTab.label.title)
    innerPanel.add(cards, BorderLayout.CENTER)

    val buttonPanel = JPanel(GridBagLayout())
    buttonPanel.addGridBag(saveButton, x = 0, y = 0, xSpan = 1)
    buttonPanel.addGridBag(launchButton, x = 1, y = 0, xSpan = 1, anchor = GridBagConstraints.WEST)
    buttonPanel.background = TabPanel.bgc
    innerPanel.add(buttonPanel, BorderLayout.SOUTH)
    innerPanel.sizeTo(TabPanel.HARD_WIDTH, HARD_HEIGHT)

    add(innerPanel, BorderLayout.EAST)

    isResizable = false
    isVisible = true
  }

  override fun actionPerformed(e: ActionEvent) = processSource(e.source)
  override fun keyPressed(e: KeyEvent) {}
  override fun keyReleased(e: KeyEvent) {}
  override fun keyTyped(e: KeyEvent) = processSource(e.source)

  private fun processSource(obj: Any) = when (obj) {
    saveButton               -> save()
    launchButton             -> launch()

    fileOptionsTab.label     -> (cards.layout as CardLayout).show(cards, fileOptionsTab.label.title)
    displayOptionsTab.label  -> (cards.layout as CardLayout).show(cards, displayOptionsTab.label.title)
    advancedOptionsTab.label -> (cards.layout as CardLayout).show(cards, advancedOptionsTab.label.title)

    else                     -> println("Miss for ${obj::class}: $obj")
  }

  private fun save() {
    ENV.writeLock = true

    ENV.archive = archive.text
    ENV.root = root.text
    //    ENV.imagePattern = Regex(imagePattern.text)

    ENV.ordering = ordering.selectedItem as String
    ENV.fontName = fontName.selectedItem as String
    ENV.scaling = CachedImage.SCALING_REMAP[scaling.selectedItem]!!

    ENV.speed = speed.value
    ENV.debounce = debounce.value.toLong()
    ENV.imageBufferCapacity = imageBufferCapacity.value
    ENV.intraPlaylistVision = intraPlaylistVision.value

    ENV.direction = directionGroup.selectedItem == LEFT_TO_RIGHT_TEXT
    ENV.paneled = paneledInput.isSelected
    ENV.showFooterFileNumber = showFooterFileNumberInput.isSelected
    ENV.showMarginFileCount = showMarginFileCountInput.isSelected
    ENV.showMarginFileName = showMarginFileNameInput.isSelected
    ENV.showMarginFolderCount = showMarginFolderCountInput.isSelected
    ENV.showMarginFolderName = showMarginFolderNameInput.isSelected
    ENV.verbose = verboseInput.isSelected

    ENV.writeLock = false
    ENV.save()
  }

  private fun launch() {
    save()
    Projector()
    dispose()
  }
}