package slideshow

import storage.ENV
import utils.extensions.CACHED_FILE
import utils.extensions.CACHED_PATH
import utils.extensions.CACHE_FULL_IMAGE
import utils.extensions.CACHE_RESIZED_IMAGE
import utils.extensions.dimension
import utils.extensions.div
import utils.extensions.lazyCache
import utils.extensions.times
import utils.extensions.width
import utils.inheritors.Geometry
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CachedImage(val file: File, private val frame: Projector) : Geometry, Comparable<CachedImage> {
  override val geometryType = "slideshow.CachedImage"
  val rawBytes: Long           by lazyCache { file.length() }
  var fullImage: BufferedImage? by lazyCache { ImageIO.read(file) }
  var sizedImage: Image?         by lazyCache(::resizeFullImage)
  var drawPosition: Dimension? = null
  var cacheLevel: Int = CACHED_FILE
    set(next) {
      when (next) {
        CACHED_PATH         -> {
        }
        CACHED_FILE         -> {
          fullImage = null; sizedImage = null
        }
        CACHE_FULL_IMAGE    -> fullImage
        CACHE_RESIZED_IMAGE -> sizedImage
      }
    }

  val width: Int by lazy { sizedImage!!.width }

  companion object {
    val SCALING_OPTIONS = hashMapOf(
      Image.SCALE_AREA_AVERAGING to "Area Average",
      Image.SCALE_DEFAULT to "Default",
      Image.SCALE_FAST to "Fast",
      Image.SCALE_SMOOTH to "Smooth",
      Image.SCALE_REPLICATE to "Replicate"
    )

    val SCALING_REMAP = hashMapOf(
      "Area Average" to Image.SCALE_AREA_AVERAGING,
      "Default" to Image.SCALE_DEFAULT,
      "Fast" to Image.SCALE_FAST,
      "Smooth" to Image.SCALE_SMOOTH,
      "Replicate" to Image.SCALE_REPLICATE
    )
  }

  private fun resizeFullImage(): Image {
    val adjustedDimension = fullImage!!.dimension * (frame.size / fullImage!!.dimension)
    if (fullImage!!.dimension == adjustedDimension) return fullImage!!
    return fullImage!!.getScaledInstance(adjustedDimension.width, adjustedDimension.height, ENV.scaling)
  }

  override fun paint(g: Graphics?) {
    g?.drawImage(sizedImage, drawPosition!!.width, drawPosition!!.height, null)
    g?.color = Color.RED
    if (ENV.showFooterFileNumber)
      g?.drawString(file.nameWithoutExtension, drawPosition!!.width + 5, frame.height - 10)
  }

  override fun build(xOffset: Int, yOffset: Int): Geometry {
    drawPosition = Dimension(xOffset, yOffset)
    return this
  }

  override fun compareTo(other: CachedImage) = compareValuesBy(this, other) { it.file.absolutePath }
}



