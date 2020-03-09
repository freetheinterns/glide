package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import java.awt.Color

@Serializer(forClass = Color::class)
object ColorSerializer {
  private val name = Color::class.qualifiedName!!

  override val descriptor = SerialDescriptor(name) {
    element("red", PrimitiveDescriptor("$name.red", PrimitiveKind.INT))
    element("green", PrimitiveDescriptor("$name.green", PrimitiveKind.INT))
    element("blue", PrimitiveDescriptor("$name.blue", PrimitiveKind.INT))
    element("alpha", PrimitiveDescriptor("$name.alpha", PrimitiveKind.INT))
  }

  override fun serialize(encoder: Encoder, value: Color) {
    encodeStructure(encoder) {
      it.encodeIntElement(descriptor, 0, value.red)
      it.encodeIntElement(descriptor, 1, value.green)
      it.encodeIntElement(descriptor, 2, value.blue)
      it.encodeIntElement(descriptor, 3, value.alpha)
    }
  }

  override fun deserialize(decoder: Decoder): Color =
    decodeStructure(decoder) {
      lateinit var r: Number
      lateinit var g: Number
      lateinit var b: Number
      lateinit var a: Number
      loop@ while (true) {
        when (val index = it.decodeElementIndex(RegexSerializer.descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0 -> r = it.decodeIntElement(descriptor, 0)
          1 -> g = it.decodeIntElement(descriptor, 1)
          2 -> b = it.decodeIntElement(descriptor, 2)
          3 -> a = it.decodeIntElement(descriptor, 3)
          else -> throw SerializationException("Unknown index $index")
        }
      }
      Color(r.toInt(), g.toInt(), b.toInt(), a.toInt())
    }
}
