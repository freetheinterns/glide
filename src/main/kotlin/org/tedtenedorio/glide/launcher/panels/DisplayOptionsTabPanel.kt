package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.FONT_FAMILIES
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.utils.FontNameRenderer
import java.awt.Font
import javax.swing.JCheckBox
import javax.swing.JComboBox

class DisplayOptionsTabPanel(
  listener: Launcher
) : TabPanel("Display", listener) {
  companion object {
    private const val LEFT_TO_RIGHT_TEXT = Launcher.LEFT_TO_RIGHT_TEXT
    private const val RIGHT_TO_LEFT_TEXT = Launcher.RIGHT_TO_LEFT_TEXT
    private const val labelFontSize = 16
  }

  val fontName: JComboBox<String>
  val directionGroup: JComboBox<String>

  val paneledInput: JCheckBox
  val showFooterFileNumberInput: JCheckBox
  val showMarginFileCountInput: JCheckBox
  val showMarginFileNameInput: JCheckBox
  val showMarginFolderCountInput: JCheckBox
  val showMarginFolderNameInput: JCheckBox

  init {
    label("Page Orientation")
    description("The direction frames are rendered when multiple frames can be rendered")
    directionGroup = comboBox(
      arrayOf(LEFT_TO_RIGHT_TEXT, RIGHT_TO_LEFT_TEXT),
      if (ENV.direction) LEFT_TO_RIGHT_TEXT else RIGHT_TO_LEFT_TEXT
    )

    description("This must be enabled for page orientation to be applied")
    paneledInput = checkBox(":Render multiple frames", ENV.paneled)

    label("Contextual Display")
    description("Controls what text is displayed in the margin")
    showMarginFolderCountInput = checkBox(
      name = ":Folder Count",
      selected = ENV.showMarginFolderCount
    )
    showMarginFolderNameInput = checkBox(
      name = ":Folder Name",
      selected = ENV.showMarginFolderName
    )
    showMarginFileCountInput = checkBox(
      name = ":File Count",
      selected = ENV.showMarginFileCount
    )
    showMarginFileNameInput = checkBox(
      name = ":File Name",
      selected = ENV.showMarginFileName
    )
    showFooterFileNumberInput = checkBox(
      name = ":Footer File#",
      selected = ENV.showFooterFileNumber
    )
    description("Font Family")
    fontName = comboBox(FONT_FAMILIES, ENV.fontName).apply {
      font = Font(ENV.fontName, Font.BOLD, labelFontSize)
      renderer = FontNameRenderer()
    }

    spring()
  }
}