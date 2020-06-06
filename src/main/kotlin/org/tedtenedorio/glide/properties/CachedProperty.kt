package org.tedtenedorio.glide.properties

import com.github.benmanes.caffeine.cache.LoadingCache
import org.tedtenedorio.glide.extensions.error
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.storage.cache.CacheOptions
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CachedProperty<V : Any?>(
  private val cacheKey: Any,
  private val cacheOptions: CacheOptions<Any, V>.() -> Unit = {},
  private val cacheLoader: (Any) -> V
) : ReadWriteProperty<Any?, V> {

  private lateinit var declarationSiteCache: LoadingCache<Any, V>

  override fun getValue(thisRef: Any?, property: KProperty<*>): V =
    declarationSiteCache[cacheKey] ?: {
      log.error { "Unexpected null value returned from cache for ${property.name}" }
      cacheLoader(cacheKey)
    }()

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
    declarationSiteCache.put(cacheKey, value)
  }

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): CachedProperty<V> {
    val declaration = Declaration(cacheKey, property.name)

    declarationSiteCache = if (CACHES.containsKey(declaration)) {
      CACHES[declaration]!! as LoadingCache<Any, V>
    } else {
      CacheOptions<Any, V>()
        .apply(cacheOptions)
        .asLoadingCache(cacheLoader)
        .also { CACHES[declaration] = it as LoadingCache<Any, Any?> }
    }

    return this
  }

  private data class Declaration(val ref: Any?, val name: String)
  private class CacheMap<V : Any?> : HashMap<Declaration, LoadingCache<Any, V>>()

  companion object {
    private val log by logger()
    private val CACHES: CacheMap<Any?> = CacheMap()

    fun <T : Any?> Any.cache(
      cacheOptions: CacheOptions<Any, T>.() -> Unit = {},
      cacheMiss: (Any) -> T
    ) = CachedProperty(this, cacheOptions, cacheMiss)

    infix fun KProperty<*>.invalidate(ref: Any) {
      CACHES[Declaration(ref, name)]?.invalidate(ref)
    }
  }
}