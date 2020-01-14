package common.glide.storage.memoization

import common.glide.storage.Persistable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.temporal.ChronoUnit


@Serializable
class FileUpdatedAtMemoizer(
  override val data: HashMap<String, Pair<Long, Long>> = hashMapOf()
) : Memoizer<String, Long>, Persistable<FileUpdatedAtMemoizer>(serializer()) {
  @Transient
  override val timeToLive: Long = ChronoUnit.DAYS.duration.toMillis()
}