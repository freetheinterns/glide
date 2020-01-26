package common.glide.extensions

import com.sun.image.codec.jpeg.JPEGCodec
import common.glide.ENV
import common.glide.FILE_CREATED_ATS
import common.glide.FILE_UPDATED_ATS
import common.glide.enums.FolderSortStrategy
import common.glide.slideshow.Catalog
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
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
      ?.sortedBy {
        when (ENV.ordering) {
          FolderSortStrategy.Alphabetical     -> it.path
          FolderSortStrategy.NumberOfFiles    -> it.fileCount.toString()
          FolderSortStrategy.FolderCreatedAt  -> it.file.createdAt.toString()
          FolderSortStrategy.FolderAccessedAt -> it.file.accessedAt.toString()
          FolderSortStrategy.FolderUpdatedAt  -> it.file.updatedAt.toString()
          FolderSortStrategy.FolderDiskSize   -> it.folderSize.toString()
          FolderSortStrategy.Random           -> UUID.randomUUID().toString()
        }
      }
    ?: listOf()

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

val File.basicAttributes: BasicFileAttributes
  get() = Files.readAttributes(toPath(), BasicFileAttributes::class.java)

val File.createdAt: Long
  get() = FILE_CREATED_ATS.get(absolutePath) { this.basicAttributes.creationTime().toMillis() }
val File.accessedAt: Long
  get() = this.basicAttributes.lastAccessTime().toMillis()
val File.updatedAt: Long
  get() = FILE_UPDATED_ATS.get(absolutePath) { this.basicAttributes.lastModifiedTime().toMillis() }
