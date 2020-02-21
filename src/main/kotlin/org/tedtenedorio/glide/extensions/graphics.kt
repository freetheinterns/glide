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
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel


///////////////////////////////////////
// GraphicsDevice Extensions
///////////////////////////////////////

fun Graphics2D.use(block: Operation<Graphics2D>) {
  block(this)
  dispose()
}


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

fun JLabel.derive(size: Long): JLabel = apply {
  font = font.deriveFont(font.size2D + size)
}

fun box(
  vertical: Boolean = true,
  isOpaque: Boolean = true,
  builder: Box.() -> Unit = {}
) = Box(if (vertical) BoxLayout.Y_AXIS else BoxLayout.X_AXIS).also {
  it.isOpaque = isOpaque
  it.builder()
}

fun Box.spring(): Component = when ((layout as BoxLayout).axis) {
  BoxLayout.Y_AXIS -> add(Box.createVerticalGlue())
  else -> add(Box.createHorizontalGlue())
}

fun Box.gap(size: Int): Component = when ((layout as BoxLayout).axis) {
  BoxLayout.Y_AXIS -> add(Box.createRigidArea(Dimension(0, size)))
  else -> add(Box.createRigidArea(Dimension(size, 0)))
}