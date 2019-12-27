package org.fte.glide

import org.fte.glide.gui.Launcher
import org.fte.glide.scripts.defineLookAndFeel
import org.fte.glide.utils.extensions.throwable
import java.awt.GraphicsEnvironment
import java.util.logging.*


fun main(args: Array<String>) {
  defineLookAndFeel()

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

  try {
    Launcher()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}