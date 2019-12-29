package common.glide.gui.components

import common.glide.utils.extensions.sizeTo
import java.awt.event.ActionListener
import javax.swing.JButton

class Button(title: String, listener: ActionListener) : JButton(title) {
  init {
    addActionListener(listener)
    sizeTo(HARD_WIDTH, HARD_HEIGHT)
  }

  companion object {
    const val HARD_HEIGHT = 35
    const val HARD_WIDTH = 250
  }
}