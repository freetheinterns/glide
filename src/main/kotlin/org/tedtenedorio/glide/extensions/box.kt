package org.tedtenedorio.glide.extensions

import javax.swing.Box
import javax.swing.BoxLayout

fun box(
  vertical: Boolean? = null,
  isOpaque: Boolean? = null,
  axis: Int = if (vertical != false) BoxLayout.Y_AXIS else BoxLayout.X_AXIS,
  builder: Box.() -> Unit = {}
) = Box(axis).also {
  it.isOpaque = isOpaque ?: true
  it.builder()
}

fun Box.spring() {
  when ((layout as BoxLayout).axis) {
    BoxLayout.Y_AXIS -> add(Box.createVerticalGlue())
    else -> add(Box.createHorizontalGlue())
  }
}

fun Box.gap(size: Int) {
  if (size <= 0) return
  when ((layout as BoxLayout).axis) {
    BoxLayout.Y_AXIS -> add(Box.createVerticalStrut(size))
    else -> add(Box.createHorizontalStrut(size))
  }
}

fun Box.perpendicularBox(
  vertical: Boolean = (layout as BoxLayout).axis != BoxLayout.Y_AXIS,
  isOpaque: Boolean = this.isOpaque,
  builder: Box.() -> Unit = {}
) {
  add(box(
    vertical = vertical,
    isOpaque = isOpaque,
    builder = builder
  ))
}
