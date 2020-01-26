package org.tedtenedorio.glide.storage.schemas

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.tedtenedorio.glide.storage.PersistableMap
import java.time.temporal.ChronoUnit


@Serializable class FileUpdatedAtPersistableMap(
  override val version: Int = 0,
  override val data: HashMap<String, Pair<Long, Long>> = hashMapOf(),
  @Transient override val timeToLive: Long = ChronoUnit.DAYS.duration.toMillis()
) : PersistableMap<String, Long, FileUpdatedAtPersistableMap> {
  @Transient override var serializer = serializer()
}