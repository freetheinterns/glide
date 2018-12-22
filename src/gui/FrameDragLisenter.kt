package gui

import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame


class FrameDragListener(private val frame: JFrame) : MouseAdapter() {
  private var mouseDownCompCoordinates: Point? = null

  override fun mouseReleased(e: MouseEvent) {
    mouseDownCompCoordinates = null
  }

  override fun mousePressed(e: MouseEvent) {
    mouseDownCompCoordinates = e.point
  }

  override fun mouseDragged(e: MouseEvent) {
    if (mouseDownCompCoordinates == null) return
    val currCoordinates = e.locationOnScreen
    frame.setLocation(
      currCoordinates.x - mouseDownCompCoordinates!!.x,
      currCoordinates.y - mouseDownCompCoordinates!!.y
    )
  }
}