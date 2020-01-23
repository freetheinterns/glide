package common.glide.gui.listeners

import common.glide.gui.Launcher
import common.glide.quit
import common.glide.storage.Persistable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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