package org.tedtenedorio.glide.gui.listeners

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.tedtenedorio.glide.gui.Launcher
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.storage.Persistable
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE

@Serializable data class LauncherBindings(
  var launch: List<Int> = listOf(VK_ENTER),
  var exit: List<Int> = listOf(VK_ESCAPE),
  override val version: Int = 0
) : Persistable<LauncherBindings> {
  @Transient override var serializer = serializer()

  fun trigger(source: Launcher, code: Int) {
    if (launch.contains(code))
      source.launchProjector()
    if (exit.contains(code))
      quit(0)
    EventHandler.lock.unlock()
  }
}