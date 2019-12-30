package common.glide.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class FileSizeMemoizer(
  val fileSizes: HashMap<String, Pair<Long, Long>> = hashMapOf()
) : Persistable<FileSizeMemoizer>(serializer()) {
  companion object {
    const val TTL = 86400000L
  }

  fun get(key: String, cacheMiss: () -> Long): Long =
    this[key] ?: set(key, cacheMiss())

  operator fun get(key: String): Long? {
    val entry = fileSizes[key.hashCode().toString()]

    return when {
      entry == null                             -> null
      entry.second < System.currentTimeMillis() -> {
        fileSizes.remove(key.hashCode().toString())
        null
      }
      else                                      -> entry.first
    }
  }

  operator fun set(key: String, value: Long): Long = value.also {
    fileSizes[key.hashCode().toString()] = it to TTL.plus(System.currentTimeMillis())
    GlobalScope.launch(Dispatchers.IO) { save() }
  }
}
