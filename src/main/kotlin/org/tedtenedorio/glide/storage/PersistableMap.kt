package org.tedtenedorio.glide.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.tedtenedorio.glide.Loader

interface PersistableMap<K, V> {
  val data: HashMap<String, Pair<V, Long>>
  val timeToLive: Long
  val fileLock: Mutex
  val hashLock: Mutex

  fun write()

  fun get(key: K, cacheMiss: Loader<V>): V =
    this[key] ?: set(key, cacheMiss())

  operator fun get(key: K): V? {
    val entry = data[key.hashCode().toString()]

    return when {
      entry == null -> null
      entry.second < System.currentTimeMillis() -> {
        data.remove(key.hashCode().toString())
        null
      }
      else -> entry.first
    }
  }

  operator fun set(key: K, value: V): V = value.also {
    GlobalScope.launch(Dispatchers.IO) {
      launch {
        hashLock.lock()
        try {
          data[key.hashCode().toString()] = it to timeToLive.plus(System.currentTimeMillis())
        } finally {
          hashLock.unlock()
        }
      }
      launch {
        if (fileLock.tryLock()) {
          try {
            write()
          } finally {
            fileLock.unlock()
          }
        }
      }
    }
  }

  fun sanitize() {
    val keysToRemove = mutableSetOf<String>()
    val now = System.currentTimeMillis()
    for (entry in data)
      if (entry.value.second < now)
        keysToRemove.add(entry.key)
  }
}
