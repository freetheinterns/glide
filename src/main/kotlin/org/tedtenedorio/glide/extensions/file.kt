package org.tedtenedorio.glide.extensions

import com.sun.image.codec.jpeg.JPEGCodec
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.slideshow.Catalog
import org.tedtenedorio.glide.storage.cache.GlobalCaches.accessedAtCache
import org.tedtenedorio.glide.storage.cache.GlobalCaches.createdAtCache
import org.tedtenedorio.glide.storage.cache.GlobalCaches.updatedAtCache
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileFilter
import java.util.UUID
import javax.imageio.ImageIO


///////////////////////////////////////
// File Extensions
///////////////////////////////////////

val File.catalogs: List<Catalog>
  get() =
    listFiles(CatalogFilter)
      ?.toList()
      ?.map(::Catalog)
      ?.let { unordered ->
        when (ENV.ordering) {
          FolderSortStrategy.Alphabetical -> unordered.sortedBy { it.path }
          FolderSortStrategy.NumberOfFiles -> unordered.sortedBy { it.size }
          FolderSortStrategy.FolderCreatedAt -> unordered.sortedBy { it.file.createdAt }
          FolderSortStrategy.FolderAccessedAt -> unordered.sortedBy { it.file.accessedAt }
          FolderSortStrategy.FolderUpdatedAt -> unordered.sortedBy { it.file.updatedAt }
          FolderSortStrategy.FolderDiskSize -> unordered.sortedBy { it.folderSize }
          FolderSortStrategy.Random -> unordered.sortedBy { UUID.randomUUID() }
        }
      } ?: listOf()

fun File.listImages(): List<File> =
  listFiles(ImageFilter)?.toList() ?: listOf()

val File.bufferedImage: BufferedImage
  get() = try {
    ImageIO.read(this)
  } catch (exc: Exception) {
    println("Failed to load $absolutePath as buffered image.")
    JPEGCodec.createJPEGDecoder(inputStream()).decodeAsBufferedImage()
  }

///////////////////////////////////////
// File Filters
///////////////////////////////////////

val CatalogFilter = FileFilter {
  try {
    it.isDirectory && it.absolutePath != ENV.archive && File(it.absolutePath).listImages().isNotEmpty()
  } catch (ex: Exception) {
    false
  }
}

val ImageFilter = FileFilter {
  !it.isDirectory && ENV.imagePattern matches it.name
}


///////////////////////////////////////
// File Attributes
///////////////////////////////////////

val File.createdAt: Long
  get() = createdAtCache[absolutePath]!!
val File.accessedAt: Long
  get() = accessedAtCache[absolutePath]!!
val File.updatedAt: Long
  get() = updatedAtCache[absolutePath]!!
