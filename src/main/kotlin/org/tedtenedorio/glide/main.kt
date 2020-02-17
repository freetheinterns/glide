package org.tedtenedorio.glide

import org.openjdk.jmh.infra.Blackhole
import org.tedtenedorio.glide.extensions.throwable
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
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
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

internal fun defineLogger() {
  val baseLogger = Logger.getLogger("")
  baseLogger.level = Level.INFO
  baseLogger.handlers.forEach {
    it.level = Level.INFO
    it.formatter = object : Formatter() {
      override fun format(record: LogRecord): String {
        return "[${record.level.localizedName}]: ${formatMessage(record)}${record.throwable}\n"
      }
    }
  }
}

fun quit(status: Int) {
  GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  EventHandler.deRegister()
  exitProcess(status)
}

fun main(args: Array<String>) {
  defineLookAndFeel()
  defineLogger()

  try {
    EventHandler.register()
    Launcher()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}