package org.tedtenedorio.glide.listeners

import org.tedtenedorio.glide.Operation
import org.tedtenedorio.glide.extensions.minus
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


class FrameDragListener(private val moveBy: Operation<Point>) : MouseAdapter() {
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