package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import java.awt.Color

@Serializer(forClass = Color::class)
object ColorSerializer {
  private val actualSerializer: KSerializer<Map<String, Int>> =
    MapSerializer(String.serializer(), Int.serializer())

  override val descriptor = actualSerializer.descriptor

  override fun serialize(encoder: Encoder, value: Color) {
    actualSerializer.serialize(encoder, mapOf(
      "red" to value.red,
      "green" to value.green,
      "blue" to value.blue,
      "alpha" to value.alpha
    ))
  }

  override fun deserialize(decoder: Decoder): Color {
    val data = actualSerializer.deserialize(decoder)
    return Color(
      data["red"] ?: 0,
      data["green"] ?: 0,
      data["blue"] ?: 0,
      data["alpha"] ?: 0
    )
  }
}
