package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.SerialClassDescImpl
import java.awt.DisplayMode

@Serializer(DisplayMode::class)
object DisplayModeSerializer : KSerializer<DisplayMode> {
  override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Regex") {
    init {
      addElement("width")
      addElement("height")
      addElement("bitDepth")
      addElement("refreshRate")
    }
  }

  override fun deserialize(decoder: Decoder): DisplayMode =
    decodeStructure(decoder) {
      lateinit var width: Number
      lateinit var height: Number
      lateinit var bitDepth: Number
      lateinit var refreshRate: Number
      loop@ while (true) {
        when (val i = it.decodeElementIndex(descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          0                          -> width = it.decodeIntElement(descriptor, i)
          1                          -> height = it.decodeIntElement(descriptor, i)
          2                          -> bitDepth = it.decodeIntElement(descriptor, i)
          3                          -> refreshRate = it.decodeIntElement(descriptor, i)
          else                       -> throw SerializationException("Unknown index $i")
        }
      }
      DisplayMode(width.toInt(), height.toInt(), bitDepth.toInt(), refreshRate.toInt())
    }

  override fun serialize(encoder: Encoder, obj: DisplayMode) {
    encodeStructure(encoder) {
      it.encodeIntElement(descriptor, 0, obj.width)
      it.encodeIntElement(descriptor, 1, obj.height)
      it.encodeIntElement(descriptor, 2, obj.bitDepth)
      it.encodeIntElement(descriptor, 3, obj.refreshRate)
    }
  }
}