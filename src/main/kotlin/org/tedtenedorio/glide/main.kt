package org.tedtenedorio.glide

import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.listeners.EventHandler
import org.tedtenedorio.glide.scripts.defineLookAndFeel
import java.awt.GraphicsEnvironment
import kotlin.system.exitProcess

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