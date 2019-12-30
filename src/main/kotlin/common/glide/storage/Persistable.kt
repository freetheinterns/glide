package common.glide.storage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File


abstract class Persistable<T : Persistable<T>> constructor() {
  private val filename: String by lazy {
    "${this::class.simpleName}.json"
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
  } catch (exc: Exception) {
    println("Error loading ${this::class.simpleName} from file $filename")
    exc.printStackTrace()
    thisT
  }

  fun save() {
    File(filename).writeText(json.stringify(serializer, thisT))
  }
}
