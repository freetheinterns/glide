package storage

import java.io.Serializable

data class TimestampedEntry(
        val data: Serializable?,
        val ttl: Long = 0,
        val expiresAt: Long = System.currentTimeMillis() + ttl
) : Serializable {
        val isExpired: Boolean
                get() = ttl > 1 && expiresAt < System.currentTimeMillis()
}