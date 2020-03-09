package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.storage.Persist
import java.io.File
import javax.swing.JSlider

class AdvancedOptionsTabPanel(
  listener: Launcher
) : TabPanel("Advanced", listener) {
  val speed: JSlider
  val debounce: JSlider
  val imageBuffer: JSlider

  init {
    label("Slideshow")
    speed = slider(
      indicator = description("New frame every %dms"),
      min = 250,
      max = 10250,
      value = ENV.speed
    )

    label("Debounce")
    debounce = slider(
      indicator = description("One keystroke may register every %dms"),
      min = 20,
      max = 520,
      value = ENV.debounce.toInt()
    )

    label("Lookahead")
    imageBuffer = slider(
      indicator = description("<html>Affects Rendering<br/>Will display at most %d images per frame</html>"),
      min = 1,
      max = 10,
      value = ENV.maxImagesPerFrame
    )
    gap(100)
    button("Clear User Settings Folder").addActionListener {
      File(Persist.CONFIG_FOLDER).let {
        if (it.exists()) it.deleteRecursively()
      }
    }

    spring()
  }
}