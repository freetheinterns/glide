package org.tedtenedorio.glide.properties

import org.tedtenedorio.glide.Block
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ChangeTriggeringProperty<T>(
  private var value: T,
  private val callBack: Block
) : ReadWriteProperty<Any?, T> {
  override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    val trigger = this.value != value
    this.value = value
    if (trigger) callBack()
  }

  companion object {
    fun <T> blindObserver(initial: T, block: Block) = ChangeTriggeringProperty(initial, block)
  }
}