package org.tedtenedorio.glide.properties

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.tedtenedorio.glide.extensions.error
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.storage.cache.CacheOptions
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class CachedProperty<K : Any, V : Any?>(
  private val cacheKey: K,
  private val cacheOptions: CacheOptions<K, V>.() -> Unit = {},
  private val cacheLoader: (K) -> V
) : ReadWriteProperty<Any?, V> {

  private lateinit var declaration: Declaration
  private val declarationSiteCache: LoadingCache<K, V> by lazy {
    CACHES[declaration]!! as LoadingCache<K, V>
  }

  override fun getValue(thisRef: Any?, property: KProperty<*>): V =
    declarationSiteCache[cacheKey] ?: {
      log.error { "Unexpected null value returned from cache for ${property.name}" }
      cacheLoader(cacheKey)
    }()

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
    declarationSiteCache.put(cacheKey, value)
  }

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): CachedProperty<K, V> {
    // Represents the declaration site of this delegate property
    declaration = Declaration(cacheKey, property.name, property.returnType)

    if (CONFIGURATIONS[declaration] == null) {
      // If this declaration is not configured, build the cache options for it
      CONFIGURATIONS[declaration] = CacheOptions<K, V>().apply(cacheOptions) as CacheOptions<Any, Any?>
    }

    if (LOADERS[declaration] == null) {
      // If the declaration has no cache loader, set it from this instance's property
      LOADERS[declaration] = cacheLoader as (Any) -> Any?
    }

    return this
  }

  private data class Declaration(val ref: Any?, val name: String, val type: KType)

  companion object {
    private val log by logger()

    private val LOADERS: HashMap<Declaration, (Any) -> Any?> = hashMapOf()
    private val CONFIGURATIONS: HashMap<Declaration, CacheOptions<Any, Any?>> = hashMapOf()
    private val CACHES: LoadingCache<Declaration, LoadingCache<Any, Any?>> =
      Caffeine.newBuilder().build {
        CONFIGURATIONS[it]!! asLoadingCache LOADERS[it]!!
      }

    fun <K : Any, T : Any?> K.cache(
      cacheOptions: CacheOptions<K, T>.() -> Unit = {},
      cacheMiss: (K) -> T
    ) = CachedProperty(this, cacheOptions, cacheMiss)

    infix fun KProperty<*>.invalidate(ref: Any) {
      val dec = Declaration(ref, name, returnType)
      CACHES.getIfPresent(dec)?.invalidate(ref)
    }
  }
}