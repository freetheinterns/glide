package glide.storage

import glide.utils.extensions.currentThread
import java.io.Serializable


/**
 * This class implements a HashMap of Serializable content that can be saved to a file for persistence
 *
 * @property mapData HashMap<Serializable, TimestampedEntry> - Data that will be persisted
 */
abstract class FileMap : Persistable<FileMapSchema>() {
  override val filename by lazy { "${this::class.simpleName}.java.object" }
  var mapData = hashMapOf<Serializable, Serializable?>()

  init {
    load()
  }

  companion object {
    inline fun <reified T : Serializable?, F : FileMap> F.get(
      key: Serializable, cacheMiss: () -> T
    ): T = get<T>(key) ?: set(key, cacheMiss())
  }

  @Suppress("UNCHECKED_CAST")
  open operator fun <T : Serializable?> get(key: Serializable): T? = mapData[key] as? T

  open operator fun <T : Serializable?> set(target: Serializable, value: T): T = value.also {
    if (mapData[target] == value) return@also
    println("Updating ${this::class.simpleName}.$target with $value")
    if (lock.isLocked && currentThread.name == lock.lockedThreadName) {
      mapData[target] = value
    } else {
      update { mapData[target] = value }
    }
  }

  protected fun <T : Serializable?> fileData(cacheMiss: () -> T) = FileMapProperty(cacheMiss)

  final override fun toSerializedInstance() = FileMapSchema(mapData)

  final override fun updateWith(other: FileMapSchema) {
    mapData.putAll(other.mapData)
  }
}
