package common.glide.storage

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.withName

@Serializer(Regex::class)
class RegexSerializer : KSerializer<Regex> {
  override val descriptor: SerialDescriptor =
    StringDescriptor.withName("Regex")

  @Serializable
  private data class RegexData(
    val pattern: String,
    val options: Set<String> = setOf()
  ) {
    constructor(base: Regex) : this(
      base.pattern,
      base.options.map { it.name }.toSet()
    )

    val regex: Regex
      get() = Regex(
        pattern,
        options.map { RegexOption.valueOf(it) }.toSet()
      )
  }

  override fun deserialize(decoder: Decoder): Regex =
    JSON.parse(RegexData.serializer(), decoder.decodeString()).regex

  override fun serialize(encoder: Encoder, obj: Regex) {
    encoder.encodeString(JSON.stringify(RegexData.serializer(), RegexData(obj)))
  }
}