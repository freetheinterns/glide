package org.tedtenedorio.glide.storage.schemas

import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.tedtenedorio.glide.storage.Persist.save
import org.tedtenedorio.glide.storage.PersistableMap
import java.time.temporal.ChronoUnit


@Serializable
class FileSizePersistableMap(
  override val data: HashMap<String, Pair<Long, Long>> = hashMapOf(),
  @Transient override val timeToLive: Long = ChronoUnit.DAYS.duration.toMillis()
) : PersistableMap<String, Long> {
  override fun write() {
    save()
  }

  @Transient
  override val fileLock: Mutex = classLock

  @Transient
  override val hashLock: Mutex = Mutex()

  companion object {
    private val classLock = Mutex()
  }
}
