package storage

import java.io.Serializable

/**
 * This class implements a HashMap of Serializable content that can be saved to a file for persistence
 *
 * @param name String - The filename the instance will be persisted to
 * @property ttl Long - The default time-to-live for entries in map
 * @property mapData HashMap<Serializable, TimestampedEntry> - Data that will be persisted
 */
abstract class FileMapTTL(name: String, private val ttl: Long = -1L) : FileMap(name) {
  data class TimestampedEntry(val data: Serializable?, val expiresAt: Long) : Serializable

  private fun entry(target: Serializable): TimestampedEntry? {
    if (mapData[target] == null) return null
    return mapData[target] as TimestampedEntry
  }

  override fun <T : Serializable?> get(target: Serializable, cacheMiss: () -> T): T {
    if ((entry(target)?.expiresAt ?: -1) >= System.currentTimeMillis())
      @Suppress("UNCHECKED_CAST") return (entry(target)!!.data as T)

    return put(target, cacheMiss())
  }

  override fun <T : Serializable?> put(target: Serializable, value: T): T {
    val expiresAt = if (ttl < 1) Long.MAX_VALUE else System.currentTimeMillis() + ttl
    mapData[target] = TimestampedEntry(value, expiresAt)
    save()
    return value
  }
}
