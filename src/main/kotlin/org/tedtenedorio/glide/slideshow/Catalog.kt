package org.tedtenedorio.glide.slideshow

import org.tedtenedorio.glide.extensions.listImages
import org.tedtenedorio.glide.extensions.sumByLong
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import java.io.File

open class Catalog(val file: File) {
  private val cachedImages: List<CachedImage> =
    file.listImages().map(::CachedImage).sortedBy(CachedImage::path)

  val path: String = file.absolutePath
  val size: Int by lazy { cachedImages.size }
  val folderSize: Long by path.cache {
    cachedImages.sumByLong(CachedImage::byteSize)
  }

  operator fun get(index: Int) = cachedImages[index]
}