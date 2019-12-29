package common.glide.gui.listeners

import common.glide.utils.extensions.minus
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


class FrameDragListener(private val moveBy: (Point) -> Unit) : MouseAdapter() {
  private var mouseDownCompCoordinates: Point? = null

  override fun mouseReleased(e: MouseEvent) {
    mouseDownCompCoordinates = null
  }

  override fun mousePressed(e: MouseEvent) {
    mouseDownCompCoordinates = e.point
  }

  override fun mouseDragged(e: MouseEvent) {
    mouseDownCompCoordinates?.let { moveBy(e.locationOnScreen - it) }
  }
}