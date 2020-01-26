package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.launcher.Launcher
import javax.swing.JCheckBox
import javax.swing.JSlider

class AdvancedOptionsTabPanel(
  totalHeight: Int,
  listener: Launcher
) : TabPanel("Advanced", totalHeight, listener) {
  val debounce: JSlider
  val maxImagesPerFrame: JSlider
  val speed: JSlider

  val verboseInput: JCheckBox

  init {
    pad(PADDING)
    verboseInput = buildCheckBox(
      name = ":verbose",
      selected = ENV.verbose
    )
    speed = buildSlider(
      name = "Slideshow",
      min = 250,
      max = 10250,
      value = ENV.speed,
      labelFormat = "New frame every %dms"
    )
    debounce = buildSlider(
      name = "Debounce",
      min = 20,
      max = 520,
      value = ENV.debounce.toInt(),
      labelFormat = "One keystroke may register every %dms"
    )
    maxImagesPerFrame = buildSlider(
      name = "Lookahead",
      min = 1,
      max = 10,
      value = ENV.maxImagesPerFrame,
      labelFormat = "<html>Affects Rendering<br/>Will display at most %d images per frame</html>"
    )
  }
}