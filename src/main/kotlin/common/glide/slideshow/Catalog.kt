package common.glide.slideshow

import common.glide.ENV
import common.glide.FILE_SIZES
import common.glide.enums.FolderSortStrategy
import common.glide.extensions.accessedAt
import common.glide.extensions.createdAt
import common.glide.extensions.listImages
import common.glide.extensions.updatedAt
import java.io.File
import java.lang.Math.random

open class Catalog(val file: File) : Comparable<Catalog> {
  private val cachedImages: List<CachedImage> =
    file
      .listImages()
      .map(::CachedImage)
      .sorted()

  val path: String by lazy { file.absolutePath }
  val size: Int by lazy { cachedImages.size }
  val fileCount: Int by lazy { cachedImages.size }

  val folderSize: Long by lazy {
    FILE_SIZES.get(path) { cachedImages.map { it.rawBytes }.sum() }
  }


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