package org.tedtenedorio.glide.listeners

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.EVENT_DISPATCHER
import org.tedtenedorio.glide.extensions.error
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.warn
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Versionable
import java.awt.event.KeyEvent.VK_BACK_SPACE
import java.awt.event.KeyEvent.VK_DELETE
import java.awt.event.KeyEvent.VK_DOWN
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE
import java.awt.event.KeyEvent.VK_F5
import java.awt.event.KeyEvent.VK_F8
import java.awt.event.KeyEvent.VK_LEFT
import java.awt.event.KeyEvent.VK_RIGHT
import java.awt.event.KeyEvent.VK_SHIFT
import java.awt.event.KeyEvent.VK_SPACE
import java.awt.event.KeyEvent.VK_TAB
import java.awt.event.KeyEvent.VK_UP
import java.awt.event.MouseEvent.BUTTON1
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption

@Serializable
data class ProjectorBindings(
  var archiveCatalog: List<Int> = listOf(VK_F5),
  var deleteCatalog: List<Int> = listOf(VK_DELETE),
  var exit: List<Int> = listOf(VK_ESCAPE),
  var inchBackward: List<Int> = listOf(VK_BACK_SPACE, VK_DOWN),
  var inchForward: List<Int> = listOf(VK_UP),
  var nextCatalog: List<Int> = listOf(VK_TAB),
  var pageBackward: List<Int> = listOf(VK_LEFT),
  var pageForward: List<Int> = listOf(VK_ENTER, VK_SPACE, VK_RIGHT, -BUTTON1),
  var previousCatalog: List<Int> = listOf(VK_SHIFT),
  var toggleSlideshow: List<Int> = listOf(VK_F8)
) : Versionable {
  companion object {
    private val log by logger()
  }

  override val version: Int = 0

  fun trigger(source: Projector, code: Int) {
    GlobalScope.launch(EVENT_DISPATCHER) {
      if (exit.contains(code))
        quit(0)

      if (inchForward.contains(code))
        source.index += 1
      if (inchBackward.contains(code))
        source.index -= 1
      if (nextCatalog.contains(code))
        source.index.jump(1)
      if (previousCatalog.contains(code))
        source.index.jump(-1)
      if (pageForward.contains(code))
        source.next()
      if (pageBackward.contains(code))
        source.prev()

      if (toggleSlideshow.contains(code)) {
        if (source.timer.isRunning) {
          source.timer.stop()
        } else {
          source.timer.start()
          source.next()
        }
      }

      if (deleteCatalog.contains(code)) {
        source.modifyCatalogFolder(source.index.primary) {
          log.warn { "Deleting Folder: $absolutePath" }
          deleteRecursively()
        }
      }

      if (archiveCatalog.contains(code)) {
        source.modifyCatalogFolder(source.index.primary) {
          val newPath = File("${ENV.archive}\\$name").toPath()
          log.warn { "Moving Folder: $absolutePath --> $newPath" }
          if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
            log.error { "Target Path already exists! No action taken!" }
          else
            Files.move(toPath(), newPath)
        }
      }

      source.project()
      EventHandler.lock.unlock()
    }
  }
}