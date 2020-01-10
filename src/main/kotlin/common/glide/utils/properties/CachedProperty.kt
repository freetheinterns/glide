package common.glide.utils.properties

import common.glide.Loader
import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class CachedProperty<T>(private val cacheMiss: Loader<T>) : ReadWriteProperty<Any, T> {
  private var value: T? = null
  private var isCachedGlobally: Boolean = false

  private fun register(thisRef: Any, property: KProperty<*>) {
    if (isCachedGlobally) return
    isCachedGlobally = true
    CACHE_MAP[thisRef to property.name] = this
  }

  override fun getValue(thisRef: Any, property: KProperty<*>): T {
    value = value ?: cacheMiss()
    register(thisRef, property)
    return value ?: throw AssertionError("Value set to null by another thread")
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
    this.value = value
    register(thisRef, property)
  }

  companion object {
    private val CACHE_MAP: MutableMap<Pair<Any, String>, CachedProperty<*>> = hashMapOf()

    fun <T> cache(cacheMiss: Loader<T>) = CachedProperty(cacheMiss)

    fun Any.invalidateCache(property: KProperty<*>) {
      val field: Field? = property.javaField
      checkNotNull(field)
      check(CachedProperty::class.java == field.type)
      check(this::class.java == field.declaringClass)

      CACHE_MAP[this to property.name]?.value = null
    }
  }
}