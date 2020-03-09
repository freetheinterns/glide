package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import java.awt.DisplayMode

@Serializer(forClass = DisplayMode::class)
object DisplayModeSerializer {
  private val name = DisplayMode::class.qualifiedName!!

  override val descriptor = SerialDescriptor(name) {
    element("width", PrimitiveDescriptor("$name.width", PrimitiveKind.INT))
    element("height", PrimitiveDescriptor("$name.height", PrimitiveKind.INT))
    element("bitDepth", PrimitiveDescriptor("$name.bitDepth", PrimitiveKind.INT))
    element("refreshRate", PrimitiveDescriptor("$name.refreshRate", PrimitiveKind.INT))
  }

  override fun serialize(encoder: Encoder, value: DisplayMode) {
    encodeStructure(encoder) {
      it.encodeIntElement(descriptor, 0, value.width)
      it.encodeIntElement(descriptor, 1, value.height)
      it.encodeIntElement(descriptor, 2, value.bitDepth)
      it.encodeIntElement(descriptor, 3, value.refreshRate)
    }
  }

  override fun deserialize(decoder: Decoder): DisplayMode =
    decodeStructure(decoder) {
      lateinit var w: Number
      lateinit var h: Number
      lateinit var b: Number
      lateinit var r: Number
      loop@ while (true) {
        when (val index = it.decodeElementIndex(RegexSerializer.descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0 -> w = it.decodeIntElement(ColorSerializer.descriptor, 0)
          1 -> h = it.decodeIntElement(ColorSerializer.descriptor, 1)
          2 -> b = it.decodeIntElement(ColorSerializer.descriptor, 2)
          3 -> r = it.decodeIntElement(ColorSerializer.descriptor, 3)
          else -> throw SerializationException("Unknown index $index")
        }
      }
      DisplayMode(w.toInt(), h.toInt(), b.toInt(), r.toInt())
    }
}