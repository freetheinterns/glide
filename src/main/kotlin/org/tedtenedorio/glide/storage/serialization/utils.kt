package org.tedtenedorio.glide.storage.serialization

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.CompositeEncoder
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.ArrayClassDesc
import kotlinx.serialization.internal.ArrayListClassDesc
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.BooleanArraySerializer
import kotlinx.serialization.internal.BooleanDescriptor
import kotlinx.serialization.internal.BooleanSerializer
import kotlinx.serialization.internal.ByteArraySerializer
import kotlinx.serialization.internal.ByteDescriptor
import kotlinx.serialization.internal.ByteSerializer
import kotlinx.serialization.internal.CharArraySerializer
import kotlinx.serialization.internal.CommonEnumSerializer
import kotlinx.serialization.internal.DoubleArraySerializer
import kotlinx.serialization.internal.FloatArraySerializer
import kotlinx.serialization.internal.FloatDescriptor
import kotlinx.serialization.internal.FloatSerializer
import kotlinx.serialization.internal.HashMapClassDesc
import kotlinx.serialization.internal.HashMapSerializer
import kotlinx.serialization.internal.HashSetClassDesc
import kotlinx.serialization.internal.HashSetSerializer
import kotlinx.serialization.internal.IntArraySerializer
import kotlinx.serialization.internal.IntDescriptor
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.LinkedHashMapClassDesc
import kotlinx.serialization.internal.LinkedHashMapSerializer
import kotlinx.serialization.internal.LinkedHashSetClassDesc
import kotlinx.serialization.internal.LinkedHashSetSerializer
import kotlinx.serialization.internal.LongArraySerializer
import kotlinx.serialization.internal.LongDescriptor
import kotlinx.serialization.internal.LongSerializer
import kotlinx.serialization.internal.ShortArraySerializer
import kotlinx.serialization.internal.ShortDescriptor
import kotlinx.serialization.internal.ShortSerializer
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.internal.UnitDescriptor
import kotlinx.serialization.internal.UnitSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.tedtenedorio.glide.Operation
import kotlin.reflect.KCallable
import kotlin.reflect.KType

val JSON by lazy {
  Json(JsonConfiguration.Stable.copy(strictMode = false, prettyPrint = true))
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
  block: Operation<CompositeEncoder>
) {
  encoder.beginStructure(descriptor).also(block).endStructure(descriptor)
}

@InternalSerializationApi
fun KType.argumentDescriptor(index: Int): SerialDescriptor = arguments[index].type?.serialDescriptor()!!

@InternalSerializationApi
fun KType.argumentSerializer(index: Int): KSerializer<Any> = arguments[index].type?.serializer()!! as KSerializer<Any>

@InternalSerializationApi
inline fun <reified E : Enum<E>> enumSerializer(serialName: String): CommonEnumSerializer<E> =
  CommonEnumSerializer(serialName, enumValues(), enumValues<E>().map { it.name }.toTypedArray())

@InternalSerializationApi
fun KType.serializer(): KSerializer<*>? = when (classifier) {
  Boolean::class -> BooleanSerializer
  BooleanArray::class -> BooleanArraySerializer
  Byte::class -> ByteSerializer
  ByteArray::class -> ByteArraySerializer
  Char::class -> ByteSerializer
  CharArray::class -> CharArraySerializer
  Double::class -> ByteSerializer
  DoubleArray::class -> DoubleArraySerializer
  Float::class -> FloatSerializer
  FloatArray::class -> FloatArraySerializer
  Int::class -> IntSerializer
  IntArray::class -> IntArraySerializer
  Long::class -> LongSerializer
  LongArray::class -> LongArraySerializer
  Short::class -> ShortSerializer
  ShortArray::class -> ShortArraySerializer
  String::class -> StringSerializer
  Unit::class -> UnitSerializer

  // Collections
  LinkedHashMap::class -> LinkedHashMapSerializer(argumentSerializer(0), argumentSerializer(1))
  Map::class,
  HashMap::class -> HashMapSerializer(argumentSerializer(0), argumentSerializer(1))
  LinkedHashSet::class -> LinkedHashSetSerializer(argumentSerializer(0))
  List::class,
  ArrayList::class -> ArrayListSerializer(argumentSerializer(0))
  Collection::class,
  Set::class,
  HashSet::class -> HashSetSerializer(argumentSerializer(0))

  // For now ALL enums need to be explicitly listed here as we have no way to reify the final type
  RegexOption::class -> enumSerializer<RegexOption>("RegexOption")
  else -> null
}

@InternalSerializationApi
fun KType.serialDescriptor(): SerialDescriptor? =
  when (classifier) {
    Boolean::class -> BooleanDescriptor
    BooleanArray::class -> BooleanArraySerializer.descriptor
    Byte::class -> ByteDescriptor
    ByteArray::class -> ByteArraySerializer.descriptor
    Char::class -> ByteDescriptor
    CharArray::class -> CharArraySerializer.descriptor
    Double::class -> ByteDescriptor
    DoubleArray::class -> DoubleArraySerializer.descriptor
    Float::class -> FloatDescriptor
    FloatArray::class -> FloatArraySerializer.descriptor
    Int::class -> IntDescriptor
    IntArray::class -> IntArraySerializer.descriptor
    Long::class -> LongDescriptor
    LongArray::class -> LongArraySerializer.descriptor
    Short::class -> ShortDescriptor
    ShortArray::class -> ShortArraySerializer.descriptor
    String::class -> StringDescriptor
    Unit::class -> UnitDescriptor

    // Collections
    LinkedHashMap::class -> LinkedHashMapClassDesc(argumentDescriptor(0), argumentDescriptor(1))
    Map::class,
    HashMap::class -> HashMapClassDesc(argumentDescriptor(0), argumentDescriptor(1))
    LinkedHashSet::class -> LinkedHashSetClassDesc(argumentDescriptor(0))
    List::class,
    ArrayList::class -> ArrayListClassDesc(argumentDescriptor(0))
    Collection::class,
    Set::class,
    HashSet::class -> HashSetClassDesc(argumentDescriptor(0))
    Array<out Any>::class -> ArrayClassDesc(argumentDescriptor(0))

    // For now ALL enums need to be explicitly listed here as we have no way to reify the final type
    RegexOption::class -> enumSerializer<RegexOption>("RegexOption").descriptor
    else -> null
  }

@InternalSerializationApi
inline fun <reified P> CompositeEncoder.encodePropertyAt(
  descriptor: SerialDescriptor,
  propertySerializers: Map<String, KSerializer<*>>,
  source: Any,
  prop: KCallable<P>,
  index: Int
) {
  @Suppress("UNCHECKED_CAST") when (prop.returnType.classifier) {
    Boolean::class -> encodeBooleanElement(descriptor, index, prop.call(source) as Boolean)
    BooleanArray::class -> encodeSerializableElement(descriptor, index, BooleanArraySerializer, prop.call(source) as BooleanArray)
    Byte::class -> encodeByteElement(descriptor, index, prop.call(source) as Byte)
    ByteArray::class -> encodeSerializableElement(descriptor, index, ByteArraySerializer, prop.call(source) as ByteArray)
    Char::class -> encodeCharElement(descriptor, index, prop.call(source) as Char)
    CharArray::class -> encodeSerializableElement(descriptor, index, CharArraySerializer, prop.call(source) as CharArray)
    Double::class -> encodeDoubleElement(descriptor, index, prop.call(source) as Double)
    DoubleArray::class -> encodeSerializableElement(descriptor, index, DoubleArraySerializer, prop.call(source) as DoubleArray)
    Float::class -> encodeFloatElement(descriptor, index, prop.call(source) as Float)
    FloatArray::class -> encodeSerializableElement(descriptor, index, FloatArraySerializer, prop.call(source) as FloatArray)
    Int::class -> encodeIntElement(descriptor, index, prop.call(source) as Int)
    IntArray::class -> encodeSerializableElement(descriptor, index, IntArraySerializer, prop.call(source) as IntArray)
    Long::class -> encodeLongElement(descriptor, index, prop.call(source) as Long)
    LongArray::class -> encodeSerializableElement(descriptor, index, LongArraySerializer, prop.call(source) as LongArray)
    Short::class -> encodeShortElement(descriptor, index, prop.call(source) as Short)
    ShortArray::class -> encodeSerializableElement(descriptor, index, ShortArraySerializer, prop.call(source) as ShortArray)
    String::class -> encodeStringElement(descriptor, index, prop.call(source) as String)
    Unit::class -> encodeUnitElement(descriptor, index)

    // Collections
    LinkedHashMap::class,
    Map::class,
    HashMap::class,
    LinkedHashSet::class,
    List::class,
    ArrayList::class,
    Collection::class,
    Set::class,
    HashSet::class -> encodeSerializableElement(
      descriptor,
      index,
      prop.returnType.serializer()!! as KSerializer<Any>,
      prop.call(source) as Any
    )

    // For now ALL enums need to be explicitly listed here as we have no way to reify the final type
    RegexOption::class -> encodeSerializableElement(
      descriptor,
      index,
      enumSerializer("RegexOption"),
      prop.call(source) as RegexOption
    )
    else -> encodeSerializableElement(
      descriptor,
      index,
      propertySerializers[prop.name] as? KSerializer<P>
        ?: throw RuntimeException("Unable to find deserializer for $prop"),
      prop.call(source)
    )
  }
}

@InternalSerializationApi
fun CompositeDecoder.decodePropertyAt(
  descriptor: SerialDescriptor,
  propertySerializers: Map<String, KSerializer<*>>,
  prop: KCallable<*>,
  index: Int
): Any = when (prop.returnType.classifier) {
  Boolean::class -> decodeBooleanElement(descriptor, index)
  BooleanArray::class -> decodeSerializableElement(descriptor, index, BooleanArraySerializer)
  Byte::class -> decodeBooleanElement(descriptor, index)
  ByteArray::class -> decodeSerializableElement(descriptor, index, ByteArraySerializer)
  Char::class -> decodeCharElement(descriptor, index)
  CharArray::class -> decodeSerializableElement(descriptor, index, CharArraySerializer)
  Double::class -> decodeDoubleElement(descriptor, index)
  DoubleArray::class -> decodeSerializableElement(descriptor, index, DoubleArraySerializer)
  Float::class -> decodeFloatElement(descriptor, index)
  FloatArray::class -> decodeSerializableElement(descriptor, index, FloatArraySerializer)
  Int::class -> decodeIntElement(descriptor, index)
  IntArray::class -> decodeSerializableElement(descriptor, index, IntArraySerializer)
  Long::class -> decodeLongElement(descriptor, index)
  LongArray::class -> decodeSerializableElement(descriptor, index, LongArraySerializer)
  Short::class -> decodeShortElement(descriptor, index)
  ShortArray::class -> decodeSerializableElement(descriptor, index, ShortArraySerializer)
  String::class -> decodeStringElement(descriptor, index)
  Unit::class -> decodeUnitElement(descriptor, index)

  // Collections
  LinkedHashMap::class,
  Map::class,
  HashMap::class,
  LinkedHashSet::class,
  List::class,
  ArrayList::class,
  Collection::class,
  Set::class,
  HashSet::class -> decodeSerializableElement(descriptor, index, prop.returnType.serializer()!!)

  // For now ALL enums need to be explicitly listed here as we have no way to reify the final type
  RegexOption::class -> decodeSerializableElement(descriptor, index, enumSerializer<RegexOption>("RegexOption"))
  else -> decodeSerializableElement(
    descriptor,
    index,
    propertySerializers[prop.name]
      ?: throw RuntimeException("Unable to find deserializer for $prop")
  )
} ?: throw RuntimeException("Unable to decode $prop")

