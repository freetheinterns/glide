package glide.utils.inheritors

import java.awt.Graphics

interface Geometry {
  fun paint(g: Graphics?)
  fun build(xOffset: Int = 0, yOffset: Int = 0): Geometry
}