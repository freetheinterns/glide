package glide.slideshow

import glide.storage.ENV
import glide.storage.FileMap.Companion.get
import glide.storage.IOMemoizer
import glide.utils.extensions.accessedAt
import glide.utils.extensions.createdAt
import glide.utils.extensions.listImages
import glide.utils.extensions.updatedAt
import java.io.File

class Catalog(val file: File) : Comparable<Catalog> {
  private val cachedImages: List<CachedImage> = file.listImages().map(::CachedImage).sorted()
  private val fileCount: Int
    get() = cachedImages.size

  val folderSize: Long
    get() = IOMemoizer.get(path) { cachedImages.map { it.rawBytes }.sum() }
  val path: String
    get() = file.absolutePath
  val size: Int
    get() = cachedImages.size

  override fun compareTo(other: Catalog) = compareValuesBy(this, other) {
    when (ENV.ordering) {
      ENV.ALPHABETICAL    -> it.path
      ENV.FILE_COUNT      -> it.fileCount
      ENV.FOLDER_CREATED  -> it.file.createdAt
      ENV.FOLDER_ACCESSED -> it.file.accessedAt
      ENV.FOLDER_UPDATED  -> it.file.updatedAt
      ENV.FOLDER_DATA     -> it.folderSize
      else                -> it.path
    }
  }

  operator fun get(index: Int) = cachedImages[index]
}