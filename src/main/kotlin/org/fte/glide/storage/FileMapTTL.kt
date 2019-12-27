package org.fte.glide.storage

import java.io.Serializable
import java.lang.System.currentTimeMillis

/**
 *
 * @param timeToLive The number of milliseconds memoized entries remain valid for
 */
abstract class FileMapTTL(timeToLive: Long? = null) : FileMap() {
  private val ttl: Long? = if (timeToLive ?: -1 < 0) null else timeToLive

  @Suppress("UNCHECKED_CAST")
  override operator fun <T : Serializable?> get(key: Serializable): T? =
    (mapData[key] as? TimestampedEntry<T>)?.data

  override operator fun <T : Serializable?> set(target: Serializable, value: T): T =
    super.set(target, TimestampedEntry(value, ttl?.plus(currentTimeMillis()))).data!!
}
