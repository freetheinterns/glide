package org.tedtenedorio.glide.properties

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MaximizingProperty<T : Comparable<T>>(
  private var value: T
) : ReadWriteProperty<Any?, T> {
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    if (value > this.value)
      this.value = value
  }

  override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
