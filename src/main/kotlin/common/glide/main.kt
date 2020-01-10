package common.glide

import common.glide.gui.Launcher
import common.glide.scripts.defineLookAndFeel
import common.glide.slideshow.GlideVersion
import common.glide.storage.FileSizeMemoizer
import common.glide.storage.KeyBindings
import common.glide.storage.LAST_VERSION
import common.glide.storage.SlideshowSettings
import common.glide.storage.VERSION
import common.glide.utils.extensions.throwable
import java.awt.GraphicsEnvironment
import java.util.logging.*

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

fun sanitizeSavedFiles() {
  if (VERSION == LAST_VERSION) return
  println("Version miss-match between $LAST_VERSION (old) and $VERSION")
  FileSizeMemoizer().save()
  SlideshowSettings().save()
  GlideVersion(VERSION).save()
  KeyBindings().save()
}

fun main(args: Array<String>) {
  sanitizeSavedFiles()
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