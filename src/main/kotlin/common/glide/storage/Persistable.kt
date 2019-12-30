package common.glide.storage

import common.glide.storage.serialization.JSON
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths


abstract class Persistable<T : Persistable<T>> constructor() {
  private val filename: String by lazy {
    Paths.get("").toAbsolutePath().resolve("${this::class.simpleName}.json").toString()
  }
  private lateinit var serializer: KSerializer<T>
  private lateinit var json: Json

  // Only safe if T is the implementing class of [this]
  // EG: class Example() : Persistable<Example>()
  private val thisT: T
    @Suppress("UNCHECKED_CAST") get() = this as T

  constructor(
    serializer: KSerializer<T>,
    json: Json = JSON
  ) : this() {
    this.serializer = serializer
    this.json = json
  }

  val jsonString: String
    get() = json.stringify(serializer, thisT)

  fun load(): T = try {
    json.parse(serializer, File(filename).readText()).also {
      it.json = this.json
      it.serializer = this.serializer
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
    File(filename).writeText(json.stringify(serializer, thisT))
  }
}
