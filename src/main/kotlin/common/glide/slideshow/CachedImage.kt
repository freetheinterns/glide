package common.glide.slideshow

import common.glide.BLACKHOLE
import common.glide.ENV
import common.glide.enums.CacheStrategy
import common.glide.enums.CacheStrategy.CLEAR
import common.glide.enums.CacheStrategy.ORIGINAL
import common.glide.enums.CacheStrategy.SCALED
import common.glide.extensions.scaleToFit
import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidateCache
import common.glide.utils.TriggeringProperty
import common.glide.utils.createOutlinedTypeSetter
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CachedImage(val file: File) : Geometry, Comparable<CachedImage> {
  override var position: Dimension by TriggeringProperty(Dimension()) { updateCache(SCALED) }

  private val image: BufferedImage by cache { ImageIO.read(file) }
  private val sizedImage: BufferedImage by cache {
    ENV.projector?.size?.let { image.scaleToFit(it) } ?: image
  }

  val rawBytes: Long by lazy { file.length() }
  val width: Int by lazy { sizedImage.width }
  val height: Int by lazy { sizedImage.height }
  val name: String by lazy { file.name }

  fun updateCache(strategy: CacheStrategy) = when (strategy) {
    CLEAR    -> {
      invalidateCache(::image)
      invalidateCache(::sizedImage)
    }
    ORIGINAL -> BLACKHOLE.consume(image)
    SCALED   -> BLACKHOLE.consume(width)
  }

  override fun paint(g: Graphics2D) {
    g.translate(position.width, position.height)
    g.drawRenderedImage(sizedImage, null)

    if (!ENV.showFooterFileNumber) return

    val drawOutlinedText = g.createOutlinedTypeSetter()
    g.translate(5, sizedImage.height - 10)
    g.drawOutlinedText(file.nameWithoutExtension)
  }

  override fun compareTo(other: CachedImage) =
    compareValuesBy(this, other) { it.file.absolutePath }
}



