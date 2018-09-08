package utils.extensions

import storage.ENV
import java.io.File
import java.io.FileFilter
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes


///////////////////////////////////////
// File Extensions
///////////////////////////////////////

fun File.listMatchingDirectories(): List<File> {
  return this.listFiles(DirectoryFilter()).toList()
}

fun File.firstNotMatchingDirectory(): File {
  return this.listFiles(InverseDirectoryFilter()).first()
}

fun File.listImages(): List<File> {
  return this.listFiles(ImagesFilter()).toList()
}

fun File.writeObject(obj: Any) {
  val out = this.outputStream()
  val objectOut = ObjectOutputStream(out)
  objectOut.writeObject(obj)
  objectOut.close()
  out.close()
}

fun File.readObject(): Any {
  val inp = this.inputStream()
  val objectIn = ObjectInputStream(inp)
  val x = objectIn.readObject()
  objectIn.close()
  inp.close()
  return x
}


///////////////////////////////////////
// File Filters
///////////////////////////////////////

class DirectoryFilter : FileFilter {
  override fun accept(path: File): Boolean {
    if (!path.isDirectory) return false
    return try {
      path.absolutePath != ENV.archive
    } catch (ex: Exception) {
      false
    }
  }
}

class InverseDirectoryFilter : FileFilter {
  override fun accept(path: File): Boolean {
    if (!path.isDirectory) return false
    return try {
      path.absolutePath == ENV.archive
    } catch (ex: Exception) {
      false
    }
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