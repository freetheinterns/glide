package org.fte.glide.storage

import org.fte.glide.storage.FileMap.Companion.get
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This class represents a property set in a FileMap class that will be persisted to file
 * when the instance is saved
 *
 * @param T : Serializable?
 *  A possibly nullable value that will be stored
 * @property cacheMiss () -> T
 *  A callback function for loading a default value if the property is
 *  accessed before it is set.
 */
class FileMapProperty<T : Serializable?>(private val cacheMiss: () -> T) : ReadWriteProperty<FileMap, T> {
  @Suppress("UNCHECKED_CAST")
  override fun getValue(thisRef: FileMap, property: KProperty<*>): T =
    thisRef.get(property.name, cacheMiss as () -> Serializable?) as T

  override fun setValue(thisRef: FileMap, property: KProperty<*>, value: T) {
    thisRef[property.name] = value
  }
}
