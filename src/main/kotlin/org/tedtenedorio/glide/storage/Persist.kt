package org.tedtenedorio.glide.storage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.JsonDecodingException
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting
import org.tedtenedorio.glide.listeners.LauncherBindings
import org.tedtenedorio.glide.listeners.ProjectorBindings
import org.tedtenedorio.glide.storage.schemas.SlideshowSettings
import org.tedtenedorio.glide.storage.serialization.JSON
import org.tedtenedorio.glide.storage.serialization.YAML
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths


object Persist {
  private val FILENAMES = mutableMapOf<Any, String>()
  val CONFIG_FOLDER: String by lazy {
    System.getProperty("os.name").let { OS ->
      println(OS)
      when {
        OS.contains("win", ignoreCase = true) -> System.getProperty("user.home") + "/AppData"
        OS.contains("mac", ignoreCase = true) -> System.getProperty("user.home") + "/Library/Application Support"
        else -> System.getProperty("user.home")
      } + "/glide"
    }
  }

  var ENABLED = true
  var USE_JSON = false
  val PARSER: StringFormat
    get() = if (USE_JSON) JSON else YAML

  fun <T : Any> T.jsonString(kSerializer: KSerializer<T>): String =
    PARSER.stringify(kSerializer, this)

  inline fun <reified T : Any> T.load(): T {
    if (!ENABLED) return this
    return when (this) {
      is SlideshowSettings -> load(this as SlideshowSettings, SlideshowSettings.serializer()) as T
      is LauncherBindings -> load(this as LauncherBindings, LauncherBindings.serializer()) as T
      is ProjectorBindings -> load(this as ProjectorBindings, ProjectorBindings.serializer()) as T

      else -> throw RuntimeException("Unknown serializer for $this")
    }
  }

  @VisibleForTesting
  fun <T : Any> load(base: T, kSerializer: KSerializer<T>): T {
    return try {
      val fileData = File(base.filename).readText()
      PARSER.parse(kSerializer, fileData).also {
        if (base is Versionable && it is Versionable && base.version != it.version) {
          return save(base, kSerializer)
        }
      }
    } catch (exc: FileNotFoundException) {
      println("File ${base.filename} not found. Creating it now")
      File(base.filename).also {
        it.parentFile.mkdirs()
        it.createNewFile()
      }
      save(base, kSerializer)
    } catch (exc: JsonDecodingException) {
      println("Error loading ${this::class.simpleName} from file ${base.filename}")
      exc.printStackTrace()
      save(base, kSerializer)
    } catch (exc: Exception) {
      println("Error loading ${this::class.simpleName} from file ${base.filename}")
      exc.printStackTrace()
      save(base, kSerializer)
    }
  }

  inline fun <reified T : Any> T.save(): T = apply {
    if (!ENABLED) return this
    return when (this) {
      is SlideshowSettings -> save(this as SlideshowSettings, SlideshowSettings.serializer()) as T
      is LauncherBindings -> save(this as LauncherBindings, LauncherBindings.serializer()) as T
      is ProjectorBindings -> save(this as ProjectorBindings, ProjectorBindings.serializer()) as T

      else -> throw RuntimeException("Unknown serializer for $this")
    }
  }

  fun <T : Any> save(base: T, kSerializer: KSerializer<T>): T = base.apply {
    File(filename).writeText(PARSER.stringify(kSerializer, this))
  }

  private val <T : Any> T.filename: String
    get() = FILENAMES.getOrPut(this) {
      Paths
        .get(CONFIG_FOLDER)
        .toAbsolutePath()
        .resolve("${this::class.simpleName}.${if (USE_JSON) "json" else "yml"}")
        .toString()
    }
}
