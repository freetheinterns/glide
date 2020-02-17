package org.tedtenedorio.glide.storage

import kotlinx.serialization.KSerializer
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting
import org.tedtenedorio.glide.listeners.LauncherBindings
import org.tedtenedorio.glide.listeners.ProjectorBindings
import org.tedtenedorio.glide.storage.schemas.FileCreatedAtPersistableMap
import org.tedtenedorio.glide.storage.schemas.FileSizePersistableMap
import org.tedtenedorio.glide.storage.schemas.FileUpdatedAtPersistableMap
import org.tedtenedorio.glide.storage.schemas.SlideshowSettings
import org.tedtenedorio.glide.storage.serialization.JSON
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths


object Persist {
  private val FILENAMES = mutableMapOf<Any, String>()
  var ENABLED = false

  fun <T : Any> T.jsonString(kSerializer: KSerializer<T>): String =
    JSON.stringify(kSerializer, this)

  inline fun <reified T : Any> T.load(): T {
    if (!ENABLED) return this
    return when (this) {
      is SlideshowSettings -> load(this as SlideshowSettings, SlideshowSettings.serializer()) as T
      is LauncherBindings -> load(this as LauncherBindings, LauncherBindings.serializer()) as T
      is ProjectorBindings -> load(this as ProjectorBindings, ProjectorBindings.serializer()) as T
      is FileCreatedAtPersistableMap -> load(this as FileCreatedAtPersistableMap, FileCreatedAtPersistableMap.serializer()) as T
      is FileSizePersistableMap -> load(this as FileSizePersistableMap, FileSizePersistableMap.serializer()) as T
      is FileUpdatedAtPersistableMap -> load(this as FileUpdatedAtPersistableMap, FileUpdatedAtPersistableMap.serializer()) as T

      else -> throw RuntimeException("Unknown serializer for $this")
    }
  }

  @VisibleForTesting
  fun <T : Any> load(base: T, kSerializer: KSerializer<T>): T {
    return try {
      JSON.parse(kSerializer, File(base.filename).readText()).also {
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
      is FileCreatedAtPersistableMap -> save(this as FileCreatedAtPersistableMap, FileCreatedAtPersistableMap.serializer()) as T
      is FileSizePersistableMap -> save(this as FileSizePersistableMap, FileSizePersistableMap.serializer()) as T
      is FileUpdatedAtPersistableMap -> save(this as FileUpdatedAtPersistableMap, FileUpdatedAtPersistableMap.serializer()) as T

      else -> throw RuntimeException("Unknown serializer for $this")
    }
  }

  fun <T : Any> save(base: T, kSerializer: KSerializer<T>): T = base.apply {
    File(filename).writeText(JSON.stringify(kSerializer, this))
  }

  private val <T : Any> T.filename: String
    get() = FILENAMES.getOrPut(this) {
      Paths.get("").toAbsolutePath().resolve("${this::class.simpleName}.json").toString()
    }
}
