package org.tedtenedorio.glide.utils

import java.awt.Component
import java.awt.Font
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList


internal class FontNameRenderer : DefaultListCellRenderer() {
  override fun getListCellRendererComponent(
    list: JList<*>,
    value: Any?,
    index: Int,
    isSelected: Boolean,
    cellHasFocus: Boolean
  ): Component {
    val label = super.getListCellRendererComponent(
      list, value, index, isSelected, cellHasFocus) as JLabel
    val font = Font(value as String?, Font.PLAIN, 20)
    label.font = font
    return label
  }
}