package storage

import java.io.Serializable

data class TimestampedEntry(
        val data: Serializable?,
        val createdAt: Long = System.currentTimeMillis()
) : Serializable