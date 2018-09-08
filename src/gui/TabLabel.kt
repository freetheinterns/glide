package gui

import utils.extensions.sizeTo
import java.awt.Color
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class TabLabel(
        val title: String,
        private val listener: ActionListener
) : JPanel(), MouseListener, MouseMotionListener {
  private val label = JLabel(title)

  init {
    sizeTo(HARD_WIDTH, HARD_HEIGHT)

    border = EmptyBorder(0, 20, 0, 0)
    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
    background = colorNormal
    addMouseListener(this)
    label.foreground = Color.WHITE
    label.alignmentX = Component.CENTER_ALIGNMENT
    label.alignmentY = Component.CENTER_ALIGNMENT
    add(label)
  }

  companion object {
    val colorNormal = Color(27, 28, 27)
    val colorSelected = Color(70, 71, 71)
    const val HARD_WIDTH = 220
    const val HARD_HEIGHT = 59
  }

  override fun mouseEntered(e: MouseEvent) {
    background = colorSelected
  }

  override fun mouseMoved(e: MouseEvent) {
    background = colorSelected
  }

  override fun mouseExited(e: MouseEvent) {
    background = colorNormal
  }

  override fun mouseClicked(e: MouseEvent) {}

  override fun mousePressed(e: MouseEvent) {
    listener.actionPerformed(ActionEvent(this, 0, "Tab Clicked"))
  }

  override fun mouseReleased(e: MouseEvent) {}
  override fun mouseDragged(e: MouseEvent?) {}
}