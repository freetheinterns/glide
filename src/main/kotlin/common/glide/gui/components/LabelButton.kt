package common.glide.gui.components

import common.glide.storage.ENV
import common.glide.utils.extensions.sizeTo
import java.awt.Color
import java.awt.Component
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
  val title: String,
  private val listener: ActionListener,
  private val defaultBackground: Color = ENV.dark,
  private val defaultSelected: Color = ENV.darkSelected,
  foreground: Color = ENV.foreground,
  width: Int = HARD_WIDTH,
  height: Int = HARD_HEIGHT,
  borderObj: Border = EmptyBorder(0, 20, 0, 0),
  private val artist: (Graphics2D) -> Unit = {}
) : JPanel(), MouseListener, MouseMotionListener {
  private val label = JLabel(title)

  init {
    sizeTo(width, height)

    border = borderObj
    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
    background = defaultBackground
    addMouseListener(this)
    label.foreground = foreground
    label.alignmentX = Component.CENTER_ALIGNMENT
    label.alignmentY = Component.CENTER_ALIGNMENT
    add(label)
  }

  companion object {
    const val HARD_WIDTH = 220
    const val HARD_HEIGHT = 59
  }

  override fun paint(g: Graphics?) {
    super.paint(g)
    (g as Graphics2D?)?.let(artist)
  }

  override fun mouseEntered(e: MouseEvent) {
    background = defaultSelected
  }

  override fun mouseMoved(e: MouseEvent) {
    background = defaultSelected
  }

  override fun mouseExited(e: MouseEvent) {
    background = defaultBackground
  }

  override fun mouseClicked(e: MouseEvent) {}

  override fun mousePressed(e: MouseEvent) {
    listener.actionPerformed(ActionEvent(this, 0, "Tab Clicked"))
  }

  override fun mouseReleased(e: MouseEvent) {}
  override fun mouseDragged(e: MouseEvent?) {}
}