package common.glide.slideshow

import java.awt.Graphics2D

interface Geometry {
  fun render(g: Graphics2D) {
    val pos = g.transform
    paint(g)
    g.transform = pos
  }

  fun paint(g: Graphics2D)
  fun build(xOffset: Int = 0, yOffset: Int = 0): Geometry
}