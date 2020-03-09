package org.tedtenedorio.glide.storage.serialization

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.CompositeEncoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

val JSON by lazy {
  Json(JsonConfiguration.Stable.copy(isLenient = true, prettyPrint = true))
}

val YAML by lazy {
  Yaml(configuration = YamlConfiguration(strictMode = false))
}

fun <T : Any> KSerializer<*>.decodeStructure(
  decoder: Decoder,
  block: (CompositeDecoder) -> T
): T {
  lateinit var ret: T
  decoder.beginStructure(descriptor).also { ret = block(it) }.endStructure(descriptor)
  return ret
}

fun KSerializer<*>.encodeStructure(
  encoder: Encoder,
  block: (CompositeEncoder) -> Unit
) {
  encoder.beginStructure(descriptor).also(block).endStructure(descriptor)
}
