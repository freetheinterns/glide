package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.HashSetSerializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringSerializer

@Serializer(Regex::class)
object RegexSerializer : KSerializer<Regex> {
  override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Regex") {
    init {
      addElement("pattern")
      addElement("options")
    }
  }

  override fun deserialize(decoder: Decoder): Regex =
    decodeStructure(decoder) {
      lateinit var pattern: String
      lateinit var options: Set<String>
      loop@ while (true) {
        when (val i = it.decodeElementIndex(descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0                          -> pattern = it.decodeStringElement(descriptor, i)
          1                          -> options =
            it.decodeSerializableElement(descriptor, i, HashSetSerializer(StringSerializer))
          else                       -> throw SerializationException("Unknown index $i")
        }
      }
      Regex(pattern, options.map { name -> RegexOption.valueOf(name) }.toSet())
    }

  override fun serialize(encoder: Encoder, obj: Regex) {
    encodeStructure(encoder) {
      it.encodeStringElement(descriptor, 0, obj.pattern)
      it.encodeSerializableElement(
        descriptor,
        1,
        HashSetSerializer(StringSerializer),
        obj.options.map { opt -> opt.name }.toHashSet()
      )
    }
  }
}