package org.tedtenedorio.glide.storage.serialization

import java.awt.Color
import kotlin.reflect.KFunction4

object ColorSerializer : ClassPropertySerializer<Color>() {
  override val className = "Color"
  override val classConstructor: KFunction4<Int, Int, Int, Int, Color> = ::Color
  override val properties = listOf(
    Color::getRed,
    Color::getGreen,
    Color::getBlue,
    Color::getAlpha
  )
}
