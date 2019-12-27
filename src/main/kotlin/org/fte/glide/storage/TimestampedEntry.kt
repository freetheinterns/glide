package org.fte.glide.storage

import java.io.Serializable

data class TimestampedEntry<T>(
  private val _data: T,
  val expiresAt: Long? = null
) : Serializable {
  val data: T?
    get() = if (expiresAt ?: -1 < System.currentTimeMillis()) null else _data
}
