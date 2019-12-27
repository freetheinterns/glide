package org.fte.glide.gui.panels

import org.fte.glide.gui.Launcher
import org.fte.glide.storage.ENV
import javax.swing.JCheckBox
import javax.swing.JSlider

class AdvancedOptionsTabPanel(
        totalHeight: Int,
        listener: Launcher
) : TabPanel("Advanced", totalHeight, listener) {
  val debounce: JSlider
  val imageBufferCapacity: JSlider
  val intraPlaylistVision: JSlider
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
    intraPlaylistVision = buildSlider(
      name = "Lookahead",
      min = 5,
      max = 105,
      value = ENV.intraPlaylistVision,
      labelFormat = "<html>Affects CPU<br/>Caching enforced within %d frames</html>"
    )
    imageBufferCapacity = buildSlider(
      name = "Buffer",
      min = 2,
      max = 10,
      value = ENV.imageBufferCapacity,
      labelFormat = "<html>Affects Memory<br/>Images buffered within %d frames</html>"
    )
  }
}