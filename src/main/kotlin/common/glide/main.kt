package common.glide

import common.glide.extensions.throwable
import common.glide.gui.Launcher
import common.glide.scripts.defineLookAndFeel
import common.glide.storage.KeyBindings
import common.glide.storage.SlideshowSettings
import common.glide.storage.memoization.FileCreatedAtMemoizer
import common.glide.storage.memoization.FileSizeMemoizer
import common.glide.storage.memoization.FileUpdatedAtMemoizer
import org.openjdk.jmh.infra.Blackhole
import java.awt.DisplayMode
import java.awt.GraphicsEnvironment
import java.util.logging.*


val ENV by lazy { SlideshowSettings().load() }
val KEY_BINDINGS by lazy { KeyBindings().load() }

val FILE_SIZES by lazy { FileSizeMemoizer().load().apply { sanitize() } }
val FILE_UPDATED_ATS by lazy { FileCreatedAtMemoizer().load().apply { sanitize() } }
val FILE_CREATED_ATS by lazy { FileUpdatedAtMemoizer().load().apply { sanitize() } }

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

fun main(args: Array<String>) {
  defineLookAndFeel()
  defineLogger()

  try {
    Launcher()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}