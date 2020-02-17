package org.tedtenedorio.glide.storage

import kotlinx.serialization.KSerializer
import org.tedtenedorio.glide.storage.serialization.JSON
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths


object Persist {
  private val FILENAMES = mutableMapOf<Any, String>()

  fun <T : Any> T.jsonString(kSerializer: KSerializer<T>): String =
    JSON.stringify(kSerializer, this)

  fun <T : Any> load(base: T, kSerializer: KSerializer<T>): T {
    return try {
      JSON.parse(kSerializer, File(base.filename).readText()).also {
        if (base is Versionable && it is Versionable && base.version != it.version) {
          return base.save(kSerializer)
        }
      }
    } catch (exc: FileNotFoundException) {
      println("File ${base.filename} not found. Creating it now")
      File(base.filename).also {
        it.parentFile.mkdirs()
        it.createNewFile()
      }
      base.save(kSerializer)
    } catch (exc: Exception) {
      println("Error loading ${this::class.simpleName} from file ${base.filename}")
      exc.printStackTrace()
      base.save(kSerializer)
    }
  }

  fun <T : Any> T.save(kSerializer: KSerializer<T>): T = apply {
    File(filename).writeText(JSON.stringify(kSerializer, this))
  }

  private val <T : Any> T.filename: String
    get() = FILENAMES.getOrPut(this) {
      Paths.get("").toAbsolutePath().resolve("${this::class.simpleName}.json").toString()
    }
}
