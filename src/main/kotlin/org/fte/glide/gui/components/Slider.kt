package org.fte.glide.gui.components

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
        step: Int = Math.max(1, (max - min) / 4),
        private val labelFormat: String = "(%d)",
        private val indicator: JLabel = JLabel(labelFormat.format(value))
) : JSlider(min, max, value), ChangeListener {

  init {
    minorTickSpacing = tick
    majorTickSpacing = step
    paintTicks = true
    paintLabels = true
    paintTrack = true
    preferredSize = Dimension(300, 45)
    font = font.deriveFont(font.size2D - 6L)
    alignmentX = Component.LEFT_ALIGNMENT
    addChangeListener(this)
  }

  override fun stateChanged(e: ChangeEvent) {
    indicator.text = labelFormat.format(value)
  }
}