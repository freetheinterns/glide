package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.ENV
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
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.border.EmptyBorder

class LabelButton(
  var title: String? = null,
  var listener: ActionListener? = null,
  var color: Color = ENV.dark,
  var textColor: Color = ENV.lightForeground,
  var hoverColor: Color = ENV.darkSelected,
  var paint: ((Graphics2D) -> Unit)? = null,
  builder: LabelButton.() -> Unit = {}
) : Box(BoxLayout.X_AXIS), MouseListener, MouseMotionListener {

  init {
    preferredSize = Dimension(HARD_WIDTH, HARD_HEIGHT)
    minimumSize = Dimension(HARD_WIDTH, HARD_HEIGHT)
    maximumSize = Dimension(HARD_WIDTH, HARD_HEIGHT)
    border = EmptyBorder(0, 20, 0, 0)
    background = color
    isOpaque = true
    addMouseListener(this)

    apply(builder)

    title?.let {
      add(JLabel(it).apply {
        foreground = textColor
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
    val artist = paint ?: return
    artist(g2)
  }

  override fun mouseEntered(e: MouseEvent) {
    background = hoverColor
  }

  override fun mouseMoved(e: MouseEvent) {
    background = hoverColor
  }

  override fun mouseExited(e: MouseEvent) {
    background = color
  }

  override fun mousePressed(e: MouseEvent) {
    listener?.actionPerformed(
      ActionEvent(this, ID_SEQUENCE++, "Clicked $title")
    )
  }

  override fun mouseClicked(e: MouseEvent) {}
  override fun mouseReleased(e: MouseEvent) {}
  override fun mouseDragged(e: MouseEvent?) {}
}