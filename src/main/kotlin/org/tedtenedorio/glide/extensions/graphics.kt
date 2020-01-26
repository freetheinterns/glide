package org.tedtenedorio.glide.extensions

import org.tedtenedorio.glide.Operation
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_RGB
import javax.swing.JComponent
import javax.swing.SpringLayout


///////////////////////////////////////
// GraphicsDevice Extensions
///////////////////////////////////////

fun Graphics2D.use(block: Operation<Graphics2D>) {
  block(this)
  dispose()
}


///////////////////////////////////////
// Layout Extensions
///////////////////////////////////////

fun SpringLayout.glue(face: String, root: Component, anchor: Component, padding: Int = 0) =
  putConstraint(face, root, padding, face, anchor)

///////////////////////////////////////
// Color Extensions
///////////////////////////////////////

val Color.invert: Color
  get() = Color(255 - red, 255 - green, 255 - blue)

///////////////////////////////////////
// Image Extensions
///////////////////////////////////////

fun BufferedImage.scaleToFit(area: Dimension): BufferedImage {
  val adjusted = dimension * (area / dimension)
  if (dimension == adjusted) return this
  val ret = BufferedImage(adjusted.width, adjusted.height, if (type == 0) TYPE_INT_RGB else type)
  ret.createGraphics().use {
    it.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
    it.drawImage(this, 0, 0, adjusted.width, adjusted.height, 0, 0, width, height, null)
  }
  return ret
}


///////////////////////////////////////
// Font Extensions
///////////////////////////////////////

val Font.string: String
  get() = "<Font: $fontName $size $style>"

///////////////////////////////////////
// Geometric Extensions
///////////////////////////////////////

fun Point.copy() = Point(x, y)
operator fun Point.plus(other: Point): Point = copy().apply { plusAssign(other) }
operator fun Point.plusAssign(other: Point) {
  x += other.x
  y += other.y
}

operator fun Point.minus(other: Point): Point = copy().apply { minusAssign(other) }
operator fun Point.minusAssign(other: Point) {
  x -= other.x
  y -= other.y
}

///////////////////////////////////////
// Container & Component Extensions
///////////////////////////////////////

fun JComponent.sizeTo(w: Int, h: Int) {
  minimumSize = Dimension(w, h)
  preferredSize = Dimension(w, h)
  size = Dimension(w, h)
}