package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.ENV
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicScrollBarUI

class CleanScrollBarUI(
  private val trackColorOverride: Color = ENV.dark,
  private val thumbColorOverride: Color = ENV.darkSelected
) : BasicScrollBarUI() {
  override fun paintTrack(g: Graphics, c: JComponent, trackBounds: Rectangle) {
    val g2 = g as Graphics2D
    g2.color = trackColorOverride
    g2.fill(trackBounds)
    g2.draw(trackBounds)
  }

  override fun paintThumb(g: Graphics, c: JComponent, thumbBounds: Rectangle) {
    val g2 = g as Graphics2D
    g2.color = thumbColorOverride
    g2.fill(thumbBounds)
  }

  override fun createIncreaseButton(orientation: Int) = JButton().apply {
    maximumSize = Dimension(0, 0)
    preferredSize = Dimension(0, 0)
    minimumSize = Dimension(0, 0)
  }

  override fun createDecreaseButton(orientation: Int) = JButton().apply {
    maximumSize = Dimension(0, 0)
    preferredSize = Dimension(0, 0)
    minimumSize = Dimension(0, 0)
  }
}