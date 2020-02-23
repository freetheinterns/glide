package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.quit
import java.awt.GraphicsEnvironment
import java.awt.event.WindowEvent
import java.awt.event.WindowEvent.WINDOW_CLOSED
import javax.swing.JFrame

open class FullScreenFrame : JFrame(
  GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
) {
  override fun processWindowEvent(e: WindowEvent) {
    super.processWindowEvent(e)
    if (e.id == WINDOW_CLOSED) quit(0)
  }
}
