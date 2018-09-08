package storage

import storage.schemas.FileMapSchema
import utils.extensions.vprintln
import utils.inheritors.Persistable
import java.io.File
import java.io.Serializable

/**
 * This class implements a HashMap of Serializable content that can be saved to a file for persistence
 *
 * @property persistedLocation File for persistence
 * @property validEntryWindow Long for the number of milliseconds and entry will be valid for
 * @property mapData HashMap<Serializable, TimestampedEntry> that will be persisted
 */
class FileMap(
        override val persistedLocation: File,
        private val validEntryWindow: Long = 86400000L // One Day
) : Persistable<FileMapSchema>(), Serializable {
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

    // If the cached item is expired, then invoke cacheMiss
    if (System.currentTimeMillis() - cached.createdAt > validEntryWindow) {
      vprintln("STALE Cache HIT for $target")
      return put(target, cacheMiss())
    }

    vprintln("Cache HIT for $target")
    @Suppress("UNCHECKED_CAST")
    return cached.data as T
  }

  fun <T : Serializable?> put(target: Serializable, value: T): T {
    vprintln("Cache WRITE for $target")
    mapData[target] = TimestampedEntry(value)
    save()
    return value
  }

  override fun toSerializedInstance() = FileMapSchema(mapData)
}
