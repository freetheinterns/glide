package common.glide.slideshow

import common.glide.BLACKHOLE
import common.glide.ENV
import common.glide.enums.CacheStrategy
import common.glide.enums.CacheStrategy.CLEAR
import common.glide.enums.CacheStrategy.SCALED
import common.glide.extensions.bufferedImage
import common.glide.extensions.logger
import common.glide.extensions.scaleToFit
import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidate
import common.glide.utils.TriggeringProperty
import common.glide.utils.createOutlinedTypeSetter
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.PriorityBlockingQueue

class CachedImage(val file: File) : Geometry, Comparable<CachedImage> {
  companion object {
    private val log by logger()
    val queue: PriorityBlockingQueue<CachedImage> = PriorityBlockingQueue(100, compareBy { it.lastFrame })
    var queueSize: Long = 0

    fun trimCache() {
      var overflow = queueSize - ENV.cacheSizeBytes
      if (overflow <= 0) return

      val toRemove = mutableListOf<CachedImage>()
      while (overflow > 0 && queue.isNotEmpty()) {
        val next = queue.poll()
        overflow -= next.rawBytes
        toRemove.add(next)
      }

      log.warning("Clearing ${toRemove.size} CachedImages from global cache")
      toRemove.forEach { it.updateCache(CLEAR) }
    }
  }

  override fun compareTo(other: CachedImage) = compareValuesBy(this, other) { it.path }
  override var position: Dimension by TriggeringProperty(Dimension()) { updateCache(SCALED) }

  private var lastFrame: Int = 0
  private val path: String by lazy { file.absolutePath }
  private val sizedImage: BufferedImage by cache {
    queue.add(this)
    queueSize += rawBytes
    log.info("Clearing $name from frame $lastFrame")
    Projector.singleton?.size?.let { file.bufferedImage.scaleToFit(it) } ?: file.bufferedImage
  }

  val rawBytes: Long by lazy { file.length() }
  val width: Int by lazy { sizedImage.width }
  val height: Int by lazy { sizedImage.height }
  val name: String by lazy { file.name }

  fun updateCache(strategy: CacheStrategy) = when (strategy) {
    CLEAR  -> {
      queue.remove(this)
      queueSize -= rawBytes
      ::sizedImage.invalidate(this)
    }
    SCALED -> {
      lastFrame = Projector.singleton?.frameCount ?: lastFrame
      BLACKHOLE.consume(width)
    }
  }

  override fun paint(g: Graphics2D) {
    g.translate(position.width, position.height)
    g.drawRenderedImage(sizedImage, null)

    if (!ENV.showFooterFileNumber) return

    val drawOutlinedText = g.createOutlinedTypeSetter()
    g.translate(5, sizedImage.height - 10)
    g.drawOutlinedText(file.nameWithoutExtension)
  }
}



