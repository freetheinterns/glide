package common.glide

import common.glide.extensions.throwable
import common.glide.gui.Launcher
import common.glide.gui.listeners.EventHandler
import common.glide.gui.listeners.LauncherBindings
import common.glide.gui.listeners.ProjectorBindings
import common.glide.scripts.defineLookAndFeel
import common.glide.storage.schemas.FileCreatedAtPersistableMap
import common.glide.storage.schemas.FileSizePersistableMap
import common.glide.storage.schemas.FileUpdatedAtPersistableMap
import common.glide.storage.schemas.SlideshowSettings
import org.openjdk.jmh.infra.Blackhole
import java.awt.DisplayMode
import java.awt.GraphicsEnvironment
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.system.exitProcess


const val GB = 1024 * 1024 * 8

val ENV by lazy { SlideshowSettings().load() }
val PROJECTOR_BINDINGS by lazy { ProjectorBindings().load() }
val LAUNCHER_BINDINGS by lazy { LauncherBindings().load() }

val FILE_SIZES by lazy { FileSizePersistableMap().load().apply { sanitize() } }
val FILE_UPDATED_ATS by lazy { FileCreatedAtPersistableMap().load().apply { sanitize() } }
val FILE_CREATED_ATS by lazy { FileUpdatedAtPersistableMap().load().apply { sanitize() } }

val BLACKHOLE = Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.")

val USER_HOME: String by lazy { System.getProperty("user.home") }
val FONT_FAMILIES: Array<String> by lazy {
  GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
}
val BEST_DISPLAY_MODES = arrayOf(
  DisplayMode(2560, 1440, 32, 0),
  DisplayMode(2560, 1440, 16, 0),
  DisplayMode(2560, 1440, 8, 0)
)

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