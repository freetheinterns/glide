package common.glide.storage

import kotlinx.serialization.Serializable

@Serializable
data class FileSizeMemoizer(
  val fileSizes: HashMap<String, Pair<Long, Long>> = hashMapOf()
) : Persistable<FileSizeMemoizer>(serializer()) {
  companion object {
    const val TTL = 86400000L
  }

  fun get(key: String, cacheMiss: () -> Long): Long {
    val entry = fileSizes[key]
    val now = System.currentTimeMillis()

    if (entry == null || entry.second < now) {
      val newValue = cacheMiss()
      fileSizes[key] = newValue to TTL.plus(now)
      return newValue
    }

    return entry.first
  }

  operator fun get(key: String): Long? {
    val entry = fileSizes[key]

    return if (entry == null || entry.second < System.currentTimeMillis()) {
      null
    } else {
      entry.first
    }
  }

  operator fun set(key: String, value: Long) {
    fileSizes[key] = value to TTL.plus(System.currentTimeMillis())
  }
}
