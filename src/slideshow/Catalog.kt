package slideshow

import storage.ENV
import storage.IOMemoizer
import utils.extensions.accessedAt
import utils.extensions.always
import utils.extensions.createdAt
import utils.extensions.listImages
import utils.extensions.updatedAt
import java.io.File

class Catalog(val file: File) : Comparable<Catalog> {
  private val cachedImages: List<CachedImage> = file.listImages().map(::CachedImage).sorted()
  private val fileCount by always { cachedImages.size }

  val folderSize by always { IOMemoizer.get(path) { cachedImages.map { it -> it.rawBytes }.sum() } }
  val path: String by always { file.absolutePath }
  val size: Int by always { cachedImages.size }

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