package common.glide.slideshow

import common.glide.storage.ENV
import common.glide.storage.FILE_SIZES
import common.glide.storage.SlideshowSettings
import common.glide.utils.extensions.accessedAt
import common.glide.utils.extensions.createdAt
import common.glide.utils.extensions.listImages
import common.glide.utils.extensions.updatedAt
import java.io.File

class Catalog(val file: File) : Comparable<Catalog> {
  private val cachedImages: List<CachedImage> = file.listImages().map(::CachedImage).sorted()
  private val fileCount: Int
    get() = cachedImages.size

  val folderSize: Long
    get() = FILE_SIZES.get(path) { cachedImages.map { it.rawBytes }.sum() }
  val path: String
    get() = file.absolutePath
  val size: Int
    get() = cachedImages.size

  override fun compareTo(other: Catalog) = compareValuesBy(this, other) {
    when (ENV.ordering) {
      SlideshowSettings.ALPHABETICAL    -> it.path
      SlideshowSettings.FILE_COUNT      -> it.fileCount
      SlideshowSettings.FOLDER_CREATED  -> it.file.createdAt
      SlideshowSettings.FOLDER_ACCESSED -> it.file.accessedAt
      SlideshowSettings.FOLDER_UPDATED  -> it.file.updatedAt
      SlideshowSettings.FOLDER_DATA     -> it.folderSize
      else                              -> it.path
    }
  }

  operator fun get(index: Int) = cachedImages[index]
}