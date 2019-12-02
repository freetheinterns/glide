package glide.storage

import glide.utils.extensions.currentThread
import glide.utils.extensions.update
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * This class implements a HashMap of Serializable content that can be saved to a file for persistence
 *
 * @property mapData HashMap<Serializable, TimestampedEntry> - Data that will be persisted
 */
abstract class FileMap : Persistable<FileMap.FileMapSchema>() {
  data class FileMapSchema(val mapData: HashMap<Serializable, Serializable?>) : Serializable

  /**
   * This class represents a property set in a FileMap class that will be persisted to file
   * when the instance is saved
   *
   * @param T : Serializable?
   *  A possibly nullable value that will be stored
   * @property cacheMiss () -> T
   *  A callback function for loading a default value if the property is
   *  accessed before it is set.
   */
  class FileMapProperty<T : Serializable?>(
    private val cacheMiss: () -> T
  ) : ReadWriteProperty<FileMap, T> {
    override fun getValue(thisRef: FileMap, property: KProperty<*>) =
      thisRef.get(property.name, cacheMiss)

    override fun setValue(thisRef: FileMap, property: KProperty<*>, value: T) {
      thisRef.put(property.name, value)
    }
  }

  override val filename by lazy { "${this::class.simpleName}.java.object" }
  var mapData = hashMapOf<Serializable, Serializable?>()

  init {
    load()
  }

  @Suppress("UNCHECKED_CAST")
  open fun <T : Serializable?> get(target: Serializable, cacheMiss: () -> T) =
    mapData[target] as T? ?: put(target, cacheMiss())

  open fun <T : Serializable?> put(target: Serializable, value: T): T = value.also {
    if (mapData[target] == value) return@also
    println("Updating ${this::class.simpleName}.$target with $value")
    if (lock.isLocked && currentThread.name == lock.lockedThreadName) {
      mapData[target] = value
    } else {
      update { mapData[target] = value }
    }
  }

  fun <T : Serializable?> fileData(cacheMiss: () -> T) = FileMapProperty(cacheMiss)

  override fun toSerializedInstance() = FileMapSchema(mapData)

  final override fun updateWith(other: FileMapSchema) {
    mapData.putAll(other.mapData)
  }
}
