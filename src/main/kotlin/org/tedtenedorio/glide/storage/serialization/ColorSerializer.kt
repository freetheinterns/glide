package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import java.awt.Color

@Serializer(Color::class)
object ColorSerializer : KSerializer<Color> {
  override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Color") {
    init {
      addElement("r")
      addElement("g")
      addElement("b")
      addElement("a")
    }
  }

  override fun deserialize(decoder: Decoder): Color =
    decodeStructure(decoder) {
      lateinit var r: Number
      lateinit var g: Number
      lateinit var b: Number
      lateinit var a: Number
      loop@ while (true) {
        when (val i = it.decodeElementIndex(descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0                          -> r = it.decodeIntElement(descriptor, i)
          1                          -> g = it.decodeIntElement(descriptor, i)
          2                          -> b = it.decodeIntElement(descriptor, i)
          3                          -> a = it.decodeIntElement(descriptor, i)
          else                       -> throw SerializationException("Unknown index $i")
        }
      }
      Color(r.toInt(), g.toInt(), b.toInt(), a.toInt())
    }

  override fun serialize(encoder: Encoder, obj: Color) {
    encodeStructure(encoder) {
      it.encodeIntElement(descriptor, 0, obj.red)
      it.encodeIntElement(descriptor, 1, obj.green)
      it.encodeIntElement(descriptor, 2, obj.blue)
      it.encodeIntElement(descriptor, 3, obj.alpha)
    }
  }
}