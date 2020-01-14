package common.glide.slideshow

import common.glide.BLACKHOLE
import common.glide.ENV
import common.glide.extensions.CACHED_FILE
import common.glide.extensions.CACHED_PATH
import common.glide.extensions.CACHE_FULL_IMAGE
import common.glide.extensions.CACHE_RESIZED_IMAGE
import common.glide.extensions.scaleToFit
import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidateCache
import common.glide.utils.createOutlinedTypeSetter
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CachedImage(val file: File) : Geometry, Comparable<CachedImage> {
  private var drawPosition = Dimension(0, 0)

  private val image: BufferedImage by cache { ImageIO.read(file) }
  private val sizedImage: BufferedImage by cache {
    ENV.projector?.size?.let { image.scaleToFit(it) } ?: image
  }

  val rawBytes: Long by lazy { file.length() }
  val width: Int by lazy { sizedImage.width }
  val height: Int by lazy { sizedImage.height }
  val name: String by lazy { file.name }

  var cacheLevel: Int = CACHED_FILE
    set(next) {
      when (next) {
        CACHED_PATH         -> Unit
        CACHED_FILE         -> {
          invalidateCache(::image)
          invalidateCache(::sizedImage)
        }
        CACHE_FULL_IMAGE    -> BLACKHOLE.consume(image)
        CACHE_RESIZED_IMAGE -> BLACKHOLE.consume(width)
      }
      field = next
    }

  override fun paint(g: Graphics2D) {
    g.translate(drawPosition.width, drawPosition.height)
    g.drawRenderedImage(sizedImage, null)

    if (!ENV.showFooterFileNumber) return

    val drawOutlinedText = g.createOutlinedTypeSetter()
    g.translate(5, sizedImage.height - 10)
    g.drawOutlinedText(file.nameWithoutExtension)
  }

  override fun build(xOffset: Int, yOffset: Int): Geometry = apply {
    drawPosition = Dimension(xOffset, yOffset)
    cacheLevel = CACHE_RESIZED_IMAGE
  }

  override fun compareTo(other: CachedImage) =
    compareValuesBy(this, other) { it.file.absolutePath }
}



