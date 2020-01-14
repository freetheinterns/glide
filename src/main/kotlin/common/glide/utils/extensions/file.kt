package common.glide.utils.extensions

import common.glide.slideshow.Catalog
import common.glide.storage.ENV
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


///////////////////////////////////////
// File Extensions
///////////////////////////////////////

val File.catalogs: Array<Catalog>
  get() =
    listFiles(CatalogFilter)
      ?.toList()
      ?.map(::Catalog)
      ?.sorted()
      ?.toTypedArray()
    ?: arrayOf()

fun File.listImages(): List<File> =
  listFiles(ImageFilter)?.toList() ?: listOf()

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
  get() = this.basicAttributes.creationTime().toMillis()
val File.accessedAt: Long
  get() = this.basicAttributes.lastAccessTime().toMillis()
val File.updatedAt: Long
  get() = this.basicAttributes.lastModifiedTime().toMillis()