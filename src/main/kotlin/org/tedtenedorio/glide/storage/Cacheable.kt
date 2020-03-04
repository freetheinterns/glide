package org.tedtenedorio.glide.storage

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tedtenedorio.glide.CACHE_DISPATCHER
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.GB
import org.tedtenedorio.glide.extensions.debug
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.sumByLong
import java.lang.System.currentTimeMillis
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

    var queueSize: Long = 0
      private set
    private val queue = PriorityBlockingQueue(
      100,
      // Lowest priority means top of queue.
      // Top of queue means removed from cache next.
      compareBy(Cacheable::priority).thenBy { -it.byteSize }
    )

    fun manageGlobalCache(
      maxBytes: Int = ENV.cacheSizeBytes,
      minimumCacheSize: Int = ENV.maxImagesPerFrame * 2
    ) {
      GlobalScope.launch(CACHE_DISPATCHER) {
        val start = currentTimeMillis()
        val toRemove = mutableListOf<Cacheable>()
        var overflow = queueSize - maxBytes
        if (overflow <= 0 || queue.size < minimumCacheSize) return@launch

        while (overflow > 0 && queue.size > minimumCacheSize.coerceAtLeast(0)) {
          val next = queue.poll()
          overflow -= next.byteSize
          toRemove.add(next)
        }

        log.debug {
          """
          Spent ${currentTimeMillis() - start}ms Updating Global Cache
          Current Cache: ${queueSize / GB.toFloat()} GB
          Clearing ${toRemove.size}/${queue.size} cached images from global queue
          Freeing Up: ${toRemove.sumByLong(Cacheable::byteSize) / GB.toFloat()} GB
          Target Maximum: ${maxBytes / GB.toFloat()} GB
          """.trimIndent()
        }

        toRemove.forEach { obj ->
          obj.clear()
          queue.remove(obj)
          queueSize -= obj.byteSize
        }
      }
    }
  }
}
