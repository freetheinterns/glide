package common.glide.storage.serialization

import common.glide.Mutator
import common.glide.Operation
import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.CompositeEncoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

val JSON by lazy {
  Json(JsonConfiguration.Stable.copy(strictMode = false, prettyPrint = true))
}

fun <T : Any> KSerializer<*>.decodeStructure(
  decoder: Decoder,
  block: Mutator<CompositeDecoder, T>
): T {
  lateinit var ret: T
  decoder.beginStructure(descriptor).also { ret = block(it) }.endStructure(descriptor)
  return ret
}

fun KSerializer<*>.encodeStructure(
  encoder: Encoder,
  block: Operation<CompositeEncoder>
) {
  encoder.beginStructure(descriptor).also(block).endStructure(descriptor)
}
