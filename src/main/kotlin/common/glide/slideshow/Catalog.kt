package common.glide.slideshow

import common.glide.storage.ENV
import common.glide.storage.FILE_SIZES
import common.glide.utils.extensions.accessedAt
import common.glide.utils.extensions.createdAt
import common.glide.utils.extensions.listImages
import common.glide.utils.extensions.updatedAt
import java.io.File
import java.lang.Math.random

class Catalog(val file: File) : Comparable<Catalog> {
  private val cachedImages: Array<CachedImage> =
    file
      .listImages()
      .map(::CachedImage)
      .sorted()
      .toTypedArray()

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
      FolderSortStrategy.Alphabetical     -> it.path
      FolderSortStrategy.NumberOfFiles    -> it.fileCount
      FolderSortStrategy.FolderCreatedAt  -> it.file.createdAt
      FolderSortStrategy.FolderAccessedAt -> it.file.accessedAt
      FolderSortStrategy.FolderUpdatedAt  -> it.file.updatedAt
      FolderSortStrategy.FolderDiskSize   -> it.folderSize
      FolderSortStrategy.Random           -> random()
    }
  }

  operator fun get(index: Int) = cachedImages[index]
}