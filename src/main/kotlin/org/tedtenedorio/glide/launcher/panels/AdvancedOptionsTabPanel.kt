package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import javax.swing.JCheckBox
import javax.swing.JSlider

class AdvancedOptionsTabPanel(
  listener: Launcher
) : TabPanel("Advanced", listener) {
  val verboseInput: JCheckBox
  val speed: JSlider
  val debounce: JSlider
  val imageBuffer: JSlider

  init {
    verboseInput = checkBox(":verbose", ENV.verbose)

    label("Slideshow")
    speed = slider(
      indicator = description("New frame every %dms"),
      min = 250,
      max = 10250,
      value = ENV.speed
    )

    label("Debounce")
    debounce = slider(
      indicator = description("<html>Affects Rendering<br/>Will display at most %d images per frame</html>"),
      min = 1,
      max = 10,
      value = ENV.maxImagesPerFrame
    )

    label("Lookahead")
    imageBuffer = slider(
      indicator = description("One keystroke may register every %dms"),
      min = 20,
      max = 520,
      value = ENV.debounce.toInt()
    )

    spring()
  }
}