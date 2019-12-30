package common.glide.storage.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.withName
import java.awt.Color

@Serializer(Color::class)
class ColorSerializer : KSerializer<Color> {
  override val descriptor: SerialDescriptor =
    StringDescriptor.withName("Color")

  @Serializable
  private data class ColorData(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int? = null
  ) {

    constructor(base: Color) : this(
      base.red,
      base.green,
      base.blue,
      base.alpha
    )

    val color: Color
      get() = when (a) {
        null -> Color(r, g, b)
        else -> Color(r, g, b, a)
      }
  }

  override fun deserialize(decoder: Decoder): Color =
    JSON.parse(ColorData.serializer(), decoder.decodeString()).color

  override fun serialize(encoder: Encoder, obj: Color) {
    encoder.encodeString(JSON.stringify(ColorData.serializer(), ColorData(obj)))
  }
}