package org.tedtenedorio.glide.slideshow.geometry

import java.awt.Dimension
import java.awt.Graphics2D

interface Geometry {
  var position: Dimension

  fun render(g: Graphics2D) {
    val pos = g.transform
    paint(g)
    g.transform = pos
  }

  fun paint(g: Graphics2D)
}