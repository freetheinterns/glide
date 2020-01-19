package common.glide.storage.memoization

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.temporal.ChronoUnit


@Serializable class FileUpdatedAtMemoizer(
  override val data: HashMap<String, Pair<Long, Long>> = hashMapOf()
) : Memoizer<String, Long, FileUpdatedAtMemoizer> {
  override val version: Int = 0
  @Transient override val timeToLive: Long = ChronoUnit.DAYS.duration.toMillis()
  @Transient override var serializer = serializer()
}