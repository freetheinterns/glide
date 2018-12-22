package storage

import storage.schemas.FileMapSchema
import utils.inheritors.Persistable
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This class implements a HashMap of Serializable content that can be saved to a file for persistence
 *
 * @property persistedLocation File for persistence
 * @property validEntryWindow Long for the number of milliseconds and entry will be valid for
 * @property mapData HashMap<Serializable, TimestampedEntry> that will be persisted
 */
open class FileMap(
  name: String,
  private val validEntryWindow: Long = -1  // Never invalidate
) : Persistable<FileMapSchema>("$name.java.object") {
  var mapData = hashMapOf<Serializable, TimestampedEntry>()

  /**
   * This function returns the cached value for the provided key OR hits the cacheMiss function for a new
   * value, then caches and returns said value. cacheMiss is also invoked if the timestamp of a cached item
   * has expired (outside of time window).
   *
   * @param target Serializable key for storing data
   * @param cacheMiss () ->  T function for returning serializable content when a cache is empty
   * @return T object that is now cached
   */
  fun <T : Serializable?> get(target: Serializable, cacheMiss: () -> T): T {
    val cached = mapData[target]
    // If no value is cached, invoke cacheMiss
    cached ?: return put(target, cacheMiss())

    // If the cached item is expired then invoke cacheMiss, otherwise cast the cached data
    return if (cached.isExpired) put(target, cacheMiss()) else
      @Suppress("UNCHECKED_CAST") (cached.data as T)
  }

  fun <T : Serializable?> put(target: Serializable, value: T): T {
    //    vprintln("Cache WRITE for $target")
    mapData[target] = TimestampedEntry(value, validEntryWindow)
    save()
    return value
  }

  inline fun <reified T : Serializable> fileData(default: T) =
    object : ReadWriteProperty<FileMap, T> {
      override fun getValue(thisRef: FileMap, property: KProperty<*>) =
        thisRef.get(property.name) { default }

      override fun setValue(thisRef: FileMap, property: KProperty<*>, value: T) {
        thisRef.put(property.name, value)
      }
    }

  override fun toSerializedInstance() = FileMapSchema(mapData)
}
