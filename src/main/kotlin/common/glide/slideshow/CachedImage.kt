package common.glide.slideshow

import common.glide.storage.ENV
import common.glide.utils.extensions.CACHED_FILE
import common.glide.utils.extensions.CACHED_PATH
import common.glide.utils.extensions.CACHE_FULL_IMAGE
import common.glide.utils.extensions.CACHE_RESIZED_IMAGE
import common.glide.utils.extensions.logger
import common.glide.utils.extensions.scaleToFit
import common.glide.utils.properties.CachedProperty.Companion.cache
import common.glide.utils.properties.CachedProperty.Companion.invalidateCache
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

  val rawBytes: Long by cache { file.length() }
  val width: Int by lazy { sizedImage.width }
  val height: Int by lazy { sizedImage.height }
  val name: String
    get() = file.name
  var cacheLevel: Int = CACHED_FILE
    set(next) {
      when (next) {
        CACHED_PATH         -> Unit
        CACHED_FILE         -> {
          invalidateCache(::image)
          invalidateCache(::sizedImage)
        }
        CACHE_FULL_IMAGE    -> image
        CACHE_RESIZED_IMAGE -> log.info("Ensure cache of $name width=$width")
      }
      field = next
    }

  companion object {
    private val log by logger()
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



