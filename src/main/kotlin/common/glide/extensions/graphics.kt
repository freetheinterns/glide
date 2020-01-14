package common.glide.extensions

import common.glide.BEST_DISPLAY_MODES
import common.glide.Operation
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.DisplayMode
import java.awt.Font
import java.awt.Graphics2D
import java.awt.GraphicsDevice
import java.awt.Point
import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.SpringLayout


///////////////////////////////////////
// GraphicsDevice Extensions
///////////////////////////////////////

val GraphicsDevice.bestDisplayMode: DisplayMode?
  get() {
    for (x in BEST_DISPLAY_MODES.indices) {
      for (i in displayModes.indices) {
        if (displayModes[i].width == BEST_DISPLAY_MODES[x].width
            && displayModes[i].height == BEST_DISPLAY_MODES[x].height
            && displayModes[i].bitDepth == BEST_DISPLAY_MODES[x].bitDepth
        ) {
          return BEST_DISPLAY_MODES[x]
        }
      }
    }
    return null
  }

fun GraphicsDevice.chooseBestDisplayMode() {
  val nextMode = bestDisplayMode
  if (nextMode != null) {
    displayMode = nextMode
  }
}

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
  val ret = BufferedImage(adjusted.width, adjusted.height, type)
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