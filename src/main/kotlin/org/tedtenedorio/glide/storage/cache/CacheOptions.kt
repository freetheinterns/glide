package org.tedtenedorio.glide.storage.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.time.Duration

class CacheOptions<K, V>(
  var maximumSize: Long? = null,
  var maximumWeight: Long? = null,
  var weigher: ((K, V) -> Int)? = null,
  var expireAfterAccess: Duration? = null,
  var expireAfterWrite: Duration? = null,
  var weakKeys: Boolean = false,
  var weakValues: Boolean = false,
  var gcAware: Boolean = false
) {
  infix fun asLoadingCache(cacheLoader: (K) -> V): LoadingCache<K, V> = Caffeine.newBuilder().also { cache ->
    maximumSize?.let { cache.maximumSize(it) }
    maximumWeight?.let { cache.maximumWeight(it) }
    weigher?.let { cache.weigher(it) }
    expireAfterAccess?.let { cache.expireAfterAccess(it) }
    expireAfterWrite?.let { cache.expireAfterWrite(it) }
    if (weakKeys) cache.weakKeys()
    if (weakValues) cache.weakValues()
    if (gcAware) cache.softValues()
  }.build(cacheLoader)
}
