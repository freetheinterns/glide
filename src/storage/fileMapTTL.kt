package storage

import java.io.Serializable

/**
 *
 * @property ttl Long
 * @constructor
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
