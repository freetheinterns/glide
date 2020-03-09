package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import java.awt.DisplayMode

@Serializer(forClass = DisplayMode::class)
object DisplayModeSerializer {
  private val actualSerializer: KSerializer<Map<String, Int>> =
    MapSerializer(String.serializer(), Int.serializer())

  override val descriptor = actualSerializer.descriptor

  override fun serialize(encoder: Encoder, value: DisplayMode) {
    actualSerializer.serialize(encoder, mapOf(
      "width" to value.width,
      "height" to value.height,
      "bitDepth" to value.bitDepth,
      "refreshRate" to value.refreshRate
    ))
  }

  override fun deserialize(decoder: Decoder): DisplayMode {
    val data = actualSerializer.deserialize(decoder)
    return DisplayMode(
      data["width"]!!,
      data["height"]!!,
      data["bitDepth"]!!,
      data["refreshRate"]!!
    )
  }
}