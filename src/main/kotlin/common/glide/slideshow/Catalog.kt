package common.glide.slideshow

import common.glide.FILE_SIZES
import common.glide.extensions.listImages
import common.glide.extensions.sumByLong
import common.glide.slideshow.geometry.CachedImage
import java.io.File

open class Catalog(val file: File) {
  private val cachedImages: List<CachedImage> =
    file.listImages().map(::CachedImage).sortedBy { it.path }

  val path: String by lazy { file.absolutePath }
  val size: Int by lazy { cachedImages.size }
  val fileCount: Int by lazy { cachedImages.size }
  val folderSize: Long by lazy {
    FILE_SIZES.get(path) {
      cachedImages.sumByLong(CachedImage::byteSize)
    }
  }

  operator fun get(index: Int) = cachedImages[index]
}