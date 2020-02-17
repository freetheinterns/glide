package org.tedtenedorio.glide.listeners

import kotlinx.serialization.Serializable
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.storage.Versionable
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE
import java.awt.event.KeyEvent.VK_TAB

@Serializable
data class LauncherBindings(
  var launch: List<Int> = listOf(VK_ENTER),
  var exit: List<Int> = listOf(VK_ESCAPE),
  var next: List<Int> = listOf(VK_TAB),
  override val version: Int = 1
) : Versionable {

  fun trigger(source: Launcher, code: Int) {
    if (launch.contains(code))
      source.launchProjector()
    if (next.contains(code))
      source.nextCard()
    if (exit.contains(code))
      quit(0)
    EventHandler.lock.unlock()
  }
}