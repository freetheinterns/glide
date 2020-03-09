package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.IntArraySerializer

@Serializer(forClass = Regex::class)
object RegexSerializer {
  override val descriptor = SerialDescriptor("Regex") {
    val name = this::class.qualifiedName
    element("pattern", PrimitiveDescriptor("$name.pattern", PrimitiveKind.STRING))
    element("options", IntArraySerializer().descriptor, isOptional = true)
  }

  override fun serialize(encoder: Encoder, value: Regex) {
    encodeStructure(encoder) {
      it.encodeStringElement(descriptor, 0, value.pattern)
      it.encodeSerializableElement(
        descriptor,
        1,
        IntArraySerializer(),
        value.options.map(RegexOption::value).toIntArray()
      )
    }
  }

  override fun deserialize(decoder: Decoder): Regex =
    decodeStructure(decoder) {
      lateinit var pat: String
      var opts = IntArray(0)
      loop@ while (true) {
        when (val index = it.decodeElementIndex(descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0 -> pat = it.decodeStringElement(descriptor, 0)
          1 -> opts = it.decodeSerializableElement(descriptor, 1, IntArraySerializer())
          else -> throw SerializationException("Unknown index $index")
        }
      }
      Regex(pat, opts.map(::findOption).toSet())
    }

  private fun findOption(target: Int): RegexOption =
    RegexOption.values().first { it.value == target }
}