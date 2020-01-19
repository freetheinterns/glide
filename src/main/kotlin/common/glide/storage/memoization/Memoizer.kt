package common.glide.storage.memoization

import common.glide.Loader
import common.glide.storage.Persistable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface Memoizer<K, V, T : Persistable<T>> : Persistable<T> {
  val data: HashMap<String, Pair<V, Long>>
  val timeToLive: Long

  fun get(key: K, cacheMiss: Loader<V>): V =
    this[key] ?: set(key, cacheMiss())

  operator fun get(key: K): V? {
    val entry = data[key.hashCode().toString()]

    return when {
      entry == null                             -> null
      entry.second < System.currentTimeMillis() -> {
        data.remove(key.hashCode().toString())
        null
      }
      else                                      -> entry.first
    }
  }

  operator fun set(key: K, value: V): V = value.also {
    data[key.hashCode().toString()] = it to timeToLive.plus(System.currentTimeMillis())
    GlobalScope.launch(Dispatchers.IO) { save() }
  }

  fun sanitize() {
    val keysToRemove = mutableSetOf<String>()
    val now = System.currentTimeMillis()
    for (entry in data)
      if (entry.value.second < now)
        keysToRemove.add(entry.key)
  }
}
