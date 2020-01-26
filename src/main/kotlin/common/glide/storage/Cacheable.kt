package common.glide.storage

import common.glide.ENV
import common.glide.GB
import common.glide.extensions.logger
import java.util.concurrent.PriorityBlockingQueue


interface Cacheable {
  var priority: Int
  val byteSize: Long
  fun clear()

  fun memoize() {
    queue.add(this)
    queueSize += byteSize
  }

  companion object {
    private val log by logger()
    private var queueSize: Long = 0
    private val queue = PriorityBlockingQueue<Cacheable>(
      100,
      compareBy(Cacheable::priority).thenBy(Cacheable::byteSize)
    )

    private fun remove(obj: Cacheable) {
      obj.clear()
      queue.remove(obj)
      queueSize -= obj.byteSize
    }

    fun manageGlobalCache(maxBytes: Int = ENV.cacheSizeBytes) {
      val toRemove = mutableListOf<Cacheable>()
      var overflow = queueSize - maxBytes
      if (overflow <= 0) return

      while (overflow > 0 && queue.isNotEmpty()) {
        val next = queue.poll()
        overflow -= next.byteSize
        toRemove.add(next)
      }

      log.info("""
          Current Cache: ${queueSize / GB.toFloat()} GB
          Clearing ${toRemove.size}/${queue.size} cached images from global queue
          Freeing Up: ${toRemove.sumBy { it.byteSize.toInt() } / GB.toFloat()} GB
          Target Maximum: ${maxBytes / GB.toFloat()} GB
          """.trimIndent()
      )

      toRemove.forEach(::remove)
    }
  }
}
