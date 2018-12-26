package glide

import glide.gui.Launcher
import glide.scripts.defineLookAndFeel
import java.awt.GraphicsEnvironment


fun main(args: Array<String>) {
  defineLookAndFeel()

  try {
    Launcher()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}