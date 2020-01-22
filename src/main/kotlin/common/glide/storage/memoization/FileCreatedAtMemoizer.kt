package common.glide.storage.memoization

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.temporal.ChronoUnit


@Serializable data class FileCreatedAtMemoizer(
  override val version: Int = 0,
  override val data: HashMap<String, Pair<Long, Long>> = hashMapOf(),
  @Transient override val timeToLive: Long = ChronoUnit.DAYS.duration.toMillis()
) : Memoizer<String, Long, FileCreatedAtMemoizer> {
  @Transient override var serializer = serializer()
}