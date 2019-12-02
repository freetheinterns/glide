package glide.storage

import glide.async.Lock
import glide.utils.extensions.properties
import glide.utils.extensions.readObject
import glide.utils.extensions.setAttribute
import glide.utils.extensions.writeObject
import java.io.File
import java.io.Serializable

/**
 * This class implements a framework for persisting attributes from a class / object to a file.
 * The generic class T (preferably a data class) will indicate which attributes it is capable of persisting.
 *
 * @param T: Serializable class that will be used to write / read from file
 * - All attributes on T must be present in the implementing class
 * - All attributes on T must match in type to the corresponding attribute in the implementing class
 * - If implemented within a class that satisfies the requirements for T, T may be reflective
 *   - E.G. data class Example(val a: Int) : Persistable<Example>
 *   - In this case, properties inherited from this class will not be persisted
 */
abstract class Persistable<T : Serializable> {
  val lock = Lock(this.hashCode())
  abstract val filename: String

  private companion object {
    private val INTERNAL_PROPERTY_NAMES = listOf("lock", "filename")
  }

  /**
   * @param other T instance that will be copied into the current instance
   */
  open fun updateWith(other: T) {
    val hasReflectiveProperties = other is Persistable<*>

    for (item in other::class.properties) {
      // Don't copy over properties if they are reflective from this class
      if (hasReflectiveProperties && (item.name in INTERNAL_PROPERTY_NAMES)) continue
      setAttribute(item.name, item.getter.call(other))
    }
  }

  /**
   * @return T instance prepared for write to file
   */
  abstract fun toSerializedInstance(): T

  /**
   * Loads an instance of T from the file location and copies attributes from T onto this instance
   * @suppress an unchecked cast to T when the file is read
   */
  fun load(saveAfter: Boolean = true) {
    lock.runLocked {
      File(filename).apply {
        if (!createNewFile())
          @Suppress("UNCHECKED_CAST") updateWith(readObject() as T)
        if (saveAfter)
          writeObject(toSerializedInstance())
      }
    }
  }
}