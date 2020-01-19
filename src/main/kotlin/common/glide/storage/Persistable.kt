package common.glide.storage

import common.glide.storage.serialization.JSON
import kotlinx.serialization.KSerializer
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths


interface Persistable<T : Persistable<T>> {
  companion object {
    private val FILENAMES = mutableMapOf<Any, String>()
  }

  private val filename: String
    get() = FILENAMES.getOrPut(this, ::generateFilename)

  // Only safe if T is the implementing class of [this]
  // EG: class Example() : Persistable<Example>()
  private val thisT: T
    @Suppress("UNCHECKED_CAST") get() = this as T

  var serializer: KSerializer<T>
  val version: Int

  val jsonString: String
    get() = JSON.stringify(serializer, thisT)

  fun load(): T = try {
    JSON.parse(serializer, File(filename).readText()).also {
      it.serializer = this.serializer
      if (it.version != this.version) {
        save()
        return thisT
      }
    }
  } catch (exc: FileNotFoundException) {
    println("File $filename not found. Creating it now")
    File(filename).also {
      it.parentFile.mkdirs()
      it.createNewFile()
    }
    save()
    thisT
  } catch (exc: Exception) {
    println("Error loading ${this::class.simpleName} from file $filename")
    exc.printStackTrace()
    save()
    thisT
  }

  fun save() {
    File(filename).writeText(JSON.stringify(serializer, thisT))
  }

  fun generateFilename(): String =
    Paths.get("").toAbsolutePath().resolve("${this::class.simpleName}.json").toString()
}
