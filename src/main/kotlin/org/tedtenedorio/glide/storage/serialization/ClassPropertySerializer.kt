package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlin.reflect.KCallable

@UseExperimental(InternalSerializationApi::class)
abstract class ClassPropertySerializer<T : Any> : KSerializer<T> {
  abstract val className: String
  abstract val properties: List<KCallable<*>>

  open val classConstructor: KCallable<T>? = null
  open val classBuilder: ((Array<Any?>) -> T)? = null
  open val classAnnotations: List<Annotation> = listOf()
  open val propertySerializers: Map<String, KSerializer<*>> = mapOf()

  final override val descriptor: SerialDescriptor by lazy(::buildDescriptor)

  final override fun serialize(encoder: Encoder, obj: T) {
    encodeStructure(encoder) {
      properties.forEachIndexed { index, prop ->
        it.encodePropertyAt(descriptor, propertySerializers, obj, prop, index)
      }
    }
  }

  final override fun deserialize(decoder: Decoder): T =
    decodeStructure(decoder) {
      val resp = mutableMapOf<KCallable<*>, Any?>()
      loop@ while (true) {
        when (val index = it.decodeElementIndex(descriptor)) {
          CompositeDecoder.READ_DONE -> break@loop
          in properties.indices -> properties[index].let { prop ->
            resp[prop] = it.decodePropertyAt(descriptor, propertySerializers, prop, index)
          }
          else -> throw SerializationException("Unknown index $index")
        }
      }
      val params = properties.map(resp::get).toTypedArray()
      classBuilder?.invoke(params)
        ?: classConstructor?.call(*params)
        ?: throw RuntimeException("You must provide either a class constructor (KCallable) or class builder (lambda)")
    }

  open fun buildDescriptor(): SerialDescriptor =
    SerialClassDescImpl(className).also { desc ->
      classAnnotations.forEach(desc::pushAnnotation)
      properties.forEach { desc.addPrimitiveProperty(it) }
    }

  private fun SerialClassDescImpl.addPrimitiveProperty(
    param: KCallable<*>
  ) {
    addElement(param.name.removePrefix("get"), param.returnType.isMarkedNullable)
    param.annotations.forEach(::pushAnnotation)
    val descriptor = param.returnType.serialDescriptor()
      ?: propertySerializers[param.name]?.descriptor
      ?: throw RuntimeException("Unable to find descriptor for $param")

    pushDescriptor(descriptor)
  }
}
