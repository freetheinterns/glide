package slideshow

import storage.ENV
import utils.extensions.CACHED_FILE
import utils.extensions.CACHED_PATH
import utils.extensions.CACHE_FULL_IMAGE
import utils.extensions.CACHE_RESIZED_IMAGE
import utils.extensions.always
import utils.extensions.cache
import utils.extensions.dimension
import utils.extensions.div
import utils.extensions.reversed
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

class CachedImage(val file: File) : Geometry, Comparable<CachedImage> {
  private var drawPosition = Dimension(0, 0)
  private var _image: BufferedImage? by cache { ImageIO.read(file) }
  private val image: BufferedImage by always { _image!! }
  val name: String by always { file.name }
  var sizedImage: Image? by cache(::resizeFullImage)
  val rawBytes: Long by cache { file.length() }
  val width: Int by lazy { sizedImage!!.width }
  var cacheLevel: Int = CACHED_FILE
    set(next) {
      when (next) {
        CACHED_PATH         -> {
        }
        CACHED_FILE         -> {
          _image = null; sizedImage = null
        }
        CACHE_FULL_IMAGE    -> image
        CACHE_RESIZED_IMAGE -> sizedImage
      }
    }

  companion object {
    val SCALING_OPTIONS = hashMapOf(
      Image.SCALE_AREA_AVERAGING to "Area Average",
      Image.SCALE_DEFAULT to "Default",
      Image.SCALE_FAST to "Fast",
      Image.SCALE_SMOOTH to "Smooth",
      Image.SCALE_REPLICATE to "Replicate"
    )

    val SCALING_REMAP = SCALING_OPTIONS.reversed

    fun nextScalingOption(): Int {
      return if (ENV.scaling == Image.SCALE_AREA_AVERAGING) Image.SCALE_DEFAULT
      else ENV.scaling * 2
    }
  }

  fun rerender() {
    sizedImage = null
    cacheLevel = CACHE_RESIZED_IMAGE
  }

  private fun resizeFullImage(): Image {
    val adjustedDimension = image.dimension * (ENV.projector.size / image.dimension)
    if (image.dimension == adjustedDimension) return image
    return image.getScaledInstance(adjustedDimension.width, adjustedDimension.height, ENV.scaling)
  }

  override fun paint(g: Graphics?) {
    g?.drawImage(sizedImage, drawPosition.width, drawPosition.height, null)
    g?.color = Color.RED
    if (ENV.showFooterFileNumber)
      g?.drawString(file.nameWithoutExtension, drawPosition.width + 5, ENV.projector.size.height - 10)
  }

  override fun build(xOffset: Int, yOffset: Int): Geometry {
    drawPosition = Dimension(xOffset, yOffset)
    return this
  }

  override fun compareTo(other: CachedImage) = compareValuesBy(this, other) { it.file.absolutePath }
}



