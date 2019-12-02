package glide.storage

import java.io.Serializable
import java.lang.System.currentTimeMillis
import kotlin.Long.Companion.MAX_VALUE

/**
 *
 * @property ttl Long
 * @constructor
 */
abstract class FileMapTTL(private val ttl: Long = -1L) : FileMap() {
  data class TimestampedEntry<T : Serializable?>(val data: T, val expiresAt: Long) : Serializable

  private fun entry(target: Serializable): TimestampedEntry<*>? {
    if (mapData[target] == null) return null
    return mapData[target] as TimestampedEntry<*>
  }

  override fun <T : Serializable?> get(target: Serializable, cacheMiss: () -> T): T {
    if ((entry(target)?.expiresAt ?: -1) >= currentTimeMillis())
      @Suppress("UNCHECKED_CAST") return (entry(target)!!.data as T)

    return put(target, cacheMiss())
  }

  override fun <T : Serializable?> put(target: Serializable, value: T): T {
    val expiresAt = if (ttl < 1) MAX_VALUE else currentTimeMillis() + ttl

    return super.put(target, TimestampedEntry(value, expiresAt)).data
  }
}
