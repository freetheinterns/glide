package glide.utils.extensions

import glide.slideshow.Catalog
import glide.storage.ENV
import java.io.File
import java.io.FileFilter
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


///////////////////////////////////////
// File Extensions
///////////////////////////////////////

val File.catalogs: List<Catalog>
  get() = this.listMatchingDirectories().map(::Catalog).sorted()

fun File.listMatchingDirectories(): List<File> =
  listFiles(DirectoryFilter())?.toList() ?: listOf()

fun File.listImages(): List<File> =
  listFiles(ImagesFilter())?.toList() ?: listOf()

fun File.writeObject(obj: Serializable) {
  outputStream().also {
    ObjectOutputStream(it).apply {
      writeObject(obj)
    }.close()
  }.close()
}

fun File.readObject(): Any {
  val obj: Any

  inputStream().also {
    ObjectInputStream(it).apply {
      obj = readObject()
    }.close()
  }.close()

  return obj
}


///////////////////////////////////////
// File Filters
///////////////////////////////////////

class DirectoryFilter : FileFilter {
  override fun accept(path: File) =
    try {
      path.isDirectory && path.absolutePath != ENV.archive && File(path.absolutePath).listImages().isNotEmpty()
    } catch (ex: Exception) {
      false
    }
}

class ImagesFilter : FileFilter {
  override fun accept(path: File): Boolean {
    return !path.isDirectory && ENV.imagePattern matches path.name
  }
}


///////////////////////////////////////
// File Attributes
///////////////////////////////////////

val File.basicAttributes: BasicFileAttributes
  get() = Files.readAttributes(this.toPath(), BasicFileAttributes::class.java)

val File.createdAt: Long
  get() = this.basicAttributes.creationTime().toMillis()
val File.accessedAt: Long
  get() = this.basicAttributes.lastAccessTime().toMillis()
val File.updatedAt: Long
  get() = this.basicAttributes.lastModifiedTime().toMillis()