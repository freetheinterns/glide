package common.glide.slideshow

import java.awt.BasicStroke
import java.awt.BasicStroke.CAP_ROUND
import java.awt.BasicStroke.JOIN_ROUND
import java.awt.Color
import java.awt.Color.BLACK
import java.awt.Color.WHITE
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.Stroke
import java.awt.font.FontRenderContext

typealias TypeSetter = Graphics2D.(String) -> Unit

private fun outlinedTypeSetter(
  font: Font,
  context: FontRenderContext,
  textColor: Color,
  outlineColor: Color
): TypeSetter = {
  val outline = font.createGlyphVector(context, it).outline
  color = outlineColor
  draw(outline)
  color = textColor
  fill(outline)
}

fun Graphics2D.createOutlinedTypeSetter(
  fontName: String = "Tahoma",
  fontStyle: Int = Font.PLAIN,
  fontSize: Int = 22,
  font: Font = Font(fontName, fontStyle, fontSize),
  strokeSize: Float = fontSize / 5f,
  strokeCap: Int = CAP_ROUND,
  strokeJoin: Int = JOIN_ROUND,
  stroke: Stroke = BasicStroke(strokeSize, strokeCap, strokeJoin),
  strokeColor: Color = BLACK,
  textColor: Color = WHITE,
  context: FontRenderContext = fontRenderContext
): TypeSetter {
  setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
  this.stroke = stroke

  return outlinedTypeSetter(font, context, textColor, strokeColor)
}
