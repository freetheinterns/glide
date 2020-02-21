package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.ENV
import java.awt.Component
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class Slider(
  min: Int,
  max: Int,
  value: Int,
  tick: Int,
  step: Int = 1.coerceAtLeast((max - min) / 4),
  private val labelFormat: String,
  val indicator: JLabel
) : JSlider(min, max, value), ChangeListener {

  init {
    background = ENV.background
    foreground = ENV.foreground
    minorTickSpacing = tick
    majorTickSpacing = step
    paintTicks = true
    paintLabels = true
    paintTrack = true
    preferredSize = Dimension(300, 50)
    font = font.deriveFont(font.size2D - 6L)
    alignmentX = Component.LEFT_ALIGNMENT
    addChangeListener(this)
    stateChanged(null)
  }

  override fun stateChanged(e: ChangeEvent?) {
    indicator.text = labelFormat.format(value)
  }
}