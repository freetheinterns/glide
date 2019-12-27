package org.fte.glide.gui.panels

import org.fte.glide.gui.Launcher
import org.fte.glide.slideshow.CachedImage
import org.fte.glide.storage.ENV
import org.fte.glide.utils.FontNameRenderer
import java.awt.Font
import javax.swing.JCheckBox
import javax.swing.JComboBox

class DisplayOptionsTabPanel(
        totalHeight: Int,
        listener: Launcher
) : TabPanel("Display", totalHeight, listener) {
  companion object {
    private const val LEFT_TO_RIGHT_TEXT = Launcher.LEFT_TO_RIGHT_TEXT
    private const val RIGHT_TO_LEFT_TEXT = Launcher.RIGHT_TO_LEFT_TEXT
    private const val labelFontSize = 16
  }

  val scaling: JComboBox<String> = buildComboBox(
    name = "Scaling",
    options = CachedImage.SCALING_OPTIONS.values.toTypedArray(),
    selected = CachedImage.SCALING_OPTIONS[ENV.scaling],
    description = "Algorithm to use resizing frames"
  )
  val directionGroup: JComboBox<String> = buildComboBox(
    name = "Page Orientation",
    options = arrayOf(LEFT_TO_RIGHT_TEXT, RIGHT_TO_LEFT_TEXT),
    selected = if (ENV.direction) LEFT_TO_RIGHT_TEXT else RIGHT_TO_LEFT_TEXT,
    description = "The direction frames are rendered when multiple frames can be rendered"
  )

  val paneledInput: JCheckBox = buildCheckBox(
    name = ":Render multiple frames",
    selected = ENV.paneled,
    description = "This must be enabled for page orientation to be applied"
  )
  val showFooterFileNumberInput: JCheckBox
  val showMarginFileCountInput: JCheckBox
  val showMarginFileNameInput: JCheckBox
  val showMarginFolderCountInput: JCheckBox
  val showMarginFolderNameInput: JCheckBox

  val fontName: JComboBox<String>

  init {
    add(Label("Contextual Display"))
    add(Description("Controls what text is displayed in the margin"))
    showMarginFolderCountInput = buildCheckBox(
      name = ":Folder Count",
      selected = ENV.showMarginFolderCount
    )
    showMarginFolderNameInput = buildCheckBox(
      name = ":Folder Name",
      selected = ENV.showMarginFolderName
    )
    showMarginFileCountInput = buildCheckBox(
      name = ":File Count",
      selected = ENV.showMarginFileCount
    )
    showMarginFileNameInput = buildCheckBox(
      name = ":File Name",
      selected = ENV.showMarginFileName
    )
    showFooterFileNumberInput = buildCheckBox(
      name = ":Footer File#",
      selected = ENV.showFooterFileNumber
    )
    fontName = buildComboBox(
      description = "Font Family",
      options = ENV.FONT_FAMILIES,
      selected = ENV.fontName
    )
    fontName.font = Font(ENV.fontName, Font.BOLD, labelFontSize)
    fontName.renderer = FontNameRenderer()
  }
}