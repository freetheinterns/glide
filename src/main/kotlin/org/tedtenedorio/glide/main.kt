package org.tedtenedorio.glide

import org.openjdk.jmh.infra.Blackhole
import org.tedtenedorio.glide.extensions.threadPoolDispatcher
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.scripts.defineLookAndFeel
import org.tedtenedorio.glide.storage.Persist.load
import org.tedtenedorio.glide.storage.PersistableMap
import org.tedtenedorio.glide.storage.schemas.FileCreatedAtPersistableMap
import org.tedtenedorio.glide.storage.schemas.FileSizePersistableMap
import org.tedtenedorio.glide.storage.schemas.FileUpdatedAtPersistableMap
import org.tedtenedorio.glide.storage.schemas.SlideshowSettings
import java.awt.GraphicsEnvironment
import kotlin.system.exitProcess


const val GB = 1024 * 1024 * 8

val ENV by lazy { SlideshowSettings().load() }

val FILE_SIZES by lazy {
  FileSizePersistableMap().load().apply(PersistableMap<String, Long>::sanitize)
}
val FILE_UPDATED_ATS by lazy {
  FileCreatedAtPersistableMap().load().apply(PersistableMap<String, Long>::sanitize)
}
val FILE_CREATED_ATS by lazy {
  FileUpdatedAtPersistableMap().load().apply(PersistableMap<String, Long>::sanitize)
}

val BLACKHOLE = Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.")

val USER_HOME: String by lazy { System.getProperty("user.home") }
val FONT_FAMILIES: Array<String> by lazy {
  GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
}

val EVENT_DISPATCHER = threadPoolDispatcher(coreThreads = 4, maxThreads = 12, name = "event-handler")
val BACKGROUND_DISPATCHER = threadPoolDispatcher(coreThreads = 4, name = "background")
val CACHE_DISPATCHER = threadPoolDispatcher(coreThreads = 2, name = "cache-manager")

fun quit(status: Int) {
  GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  EventHandler.deRegister()
  exitProcess(status)
}

fun main(args: Array<String>) {
  defineLookAndFeel()

  try {
    EventHandler.register()
    Launcher()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}