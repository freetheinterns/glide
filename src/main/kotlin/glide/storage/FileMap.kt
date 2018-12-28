package glide.storage

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

  open val name by lazy { this::class.simpleName }
  override val filename by lazy { "$name.java.object" }
  var mapData = hashMapOf<Serializable, Serializable?>()

  init {
    load()
  }

  /**
   * This function returns the cached value for the provided key OR hits the cacheMiss function for a new
   * value, then caches and returns said value. cacheMiss is also invoked if the timestamp of a cached item
   * has expired (outside of time window).
   *
   * @param target Serializable key for storing data
   * @param cacheMiss () ->  T function for returning serializable content when a cache is empty
   * @return T object that is now cached
   */
  @Suppress("UNCHECKED_CAST")
  open fun <T : Serializable?> get(target: Serializable, cacheMiss: () -> T) =
    (mapData[target] ?: put(target, cacheMiss())) as T

  open fun <T : Serializable?> put(target: Serializable, value: T): T {
    mapData[target] = value
    save()
    return value
  }

  open fun <T : Serializable?> fileData(default: T) =
    object : ReadWriteProperty<FileMap, T> {
      override fun getValue(thisRef: FileMap, property: KProperty<*>) =
        thisRef.get(property.name) { default }

      override fun setValue(thisRef: FileMap, property: KProperty<*>, value: T) {
        thisRef.put(property.name, value)
      }
    }

  override fun toSerializedInstance() = FileMapSchema(mapData)
}
