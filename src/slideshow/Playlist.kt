package slideshow

import FILE_SIZE_MEMOIZER
import storage.ENV
import utils.extensions.accessedAt
import utils.extensions.createdAt
import utils.extensions.listImages
import utils.extensions.updatedAt
import java.io.File

class Playlist(val file: File, private val frame: Projector) : Comparable<Playlist> {
  private val cachedImages: List<CachedImage> = file.listImages().map { CachedImage(it, frame) }.sorted()

  val folderSize: Long
    get() = FILE_SIZE_MEMOIZER.get(path) { cachedImages.map { it -> it.rawBytes }.sum() }
  private val fileCount: Int
    get() = cachedImages.size
  private val path: String
    get() = file.absolutePath
  val size: Int
    get() = cachedImages.size

  override fun compareTo(other: Playlist) = compareValuesBy(this, other) {
    when (ENV.ordering) {
      ENV.ALPHABETICAL    -> {
        it.path
      }
      ENV.FILE_COUNT      -> {
        it.fileCount
      }
      ENV.FOLDER_CREATED  -> {
        it.file.createdAt
      }
      ENV.FOLDER_ACCESSED -> {
        it.file.accessedAt
      }
      ENV.FOLDER_UPDATED  -> {
        it.file.updatedAt
      }
      ENV.FOLDER_DATA     -> {
        it.folderSize
      }
      else                -> {
        it.path
      }
    }
  }

  operator fun get(index: Int) = cachedImages[index]
}