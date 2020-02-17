package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.sizeTo
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

class LabelButton(
  builder: Settings.() -> Unit = {}
) : JPanel(), MouseListener, MouseMotionListener {
  val settings: Settings = Settings().apply(builder)

  class Settings {
    var title: String? = null
    var listener: ActionListener? = null
    var background: Color = ENV.dark
    var foreground: Color = ENV.lightForeground
    var hoverColor: Color = ENV.darkSelected
    var size: Dimension = Dimension(HARD_WIDTH, HARD_HEIGHT)
    var border: Border = EmptyBorder(0, 20, 0, 0)
    var paint: ((Graphics2D) -> Unit)? = null
  }

  init {
    sizeTo(settings.size)

    border = settings.border
    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
    background = settings.background
    addMouseListener(this)

    settings.title?.let {
      add(JLabel(it).apply {
        foreground = settings.foreground
        alignmentX = Component.CENTER_ALIGNMENT
        alignmentY = Component.CENTER_ALIGNMENT
      })
    }
  }

  companion object {
    const val HARD_WIDTH = 220
    const val HARD_HEIGHT = 59
    @Volatile
    var ID_SEQUENCE = 0
  }

  override fun paint(g: Graphics?) {
    super.paint(g)
    val g2 = g as? Graphics2D ?: return
    val artist = settings.paint ?: return
    artist(g2)
  }

  override fun mouseEntered(e: MouseEvent) {
    background = settings.hoverColor
  }

  override fun mouseMoved(e: MouseEvent) {
    background = settings.hoverColor
  }

  override fun mouseExited(e: MouseEvent) {
    background = settings.background
  }

  override fun mousePressed(e: MouseEvent) {
    settings.listener?.actionPerformed(
      ActionEvent(this, ID_SEQUENCE++, "Clicked ${settings.title}")
    )
  }

  override fun mouseClicked(e: MouseEvent) {}
  override fun mouseReleased(e: MouseEvent) {}
  override fun mouseDragged(e: MouseEvent?) {}
}