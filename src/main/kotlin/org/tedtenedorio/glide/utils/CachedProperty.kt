package org.tedtenedorio.glide.utils

import org.tedtenedorio.glide.Loader
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KVisibility

class CachedProperty<T>(private val cacheMiss: Loader<T>) : ReadWriteProperty<Any?, T> {
  private var value: T? = null

  override fun getValue(thisRef: Any?, property: KProperty<*>): T =
    value ?: cacheMiss().also { value = it }

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
  }

  operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): CachedProperty<T> =
    this.also { property.setCacheDelegate(thisRef, it) }

  private data class Declaration(val ref: Any?, val name: String, val visibility: KVisibility?, val type: KType)

  companion object {
    fun <T> cache(cacheMiss: Loader<T>) = CachedProperty(cacheMiss)

    fun KProperty<*>.invalidate(ref: Any?) {
      getCacheDelegate(ref)?.value = null
    }

    private val CACHE_MAP: MutableMap<Declaration, CachedProperty<*>> = hashMapOf()

    private fun KProperty<*>.setCacheDelegate(ref: Any?, delegate: CachedProperty<*>) {
      CACHE_MAP[Declaration(ref, name, visibility, returnType)] = delegate
    }

    private fun KProperty<*>.getCacheDelegate(ref: Any?): CachedProperty<*>? =
      CACHE_MAP[Declaration(ref, name, visibility, returnType)]
  }
}