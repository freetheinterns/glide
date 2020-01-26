package org.tedtenedorio.glide.listeners

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.quit
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Persistable
import java.awt.event.KeyEvent.VK_BACK_SPACE
import java.awt.event.KeyEvent.VK_DELETE
import java.awt.event.KeyEvent.VK_DOWN
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE
import java.awt.event.KeyEvent.VK_F5
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
import kotlin.concurrent.thread

@Serializable data class ProjectorBindings(
  var archiveCatalog: List<Int> = listOf(VK_F5),
  var deleteCatalog: List<Int> = listOf(VK_DELETE),
  var exit: List<Int> = listOf(VK_ESCAPE),
  var inchBackward: List<Int> = listOf(VK_BACK_SPACE, VK_DOWN),
  var inchForward: List<Int> = listOf(VK_UP),
  var nextCatalog: List<Int> = listOf(VK_TAB),
  var pageBackward: List<Int> = listOf(VK_LEFT),
  var pageForward: List<Int> = listOf(VK_ENTER, VK_RIGHT, -BUTTON1),
  var previousCatalog: List<Int> = listOf(VK_SHIFT),
  var toggleSlideshow: List<Int> = listOf(VK_SPACE)
) : Persistable<ProjectorBindings> {
  companion object {
    private val log by logger()
  }

  override val version: Int = 0
  @Transient override var serializer = serializer()

  fun trigger(source: Projector, it: Int) {
    thread(
      isDaemon = true,
      priority = 2,
      name = "ProjectorEvent-$it"
    ) {
      if (exit.contains(it))
        quit(0)

      if (inchForward.contains(it))
        source.index += 1
      if (inchBackward.contains(it))
        source.index -= 1
      if (nextCatalog.contains(it))
        source.index.jump(1)
      if (previousCatalog.contains(it))
        source.index.jump(-1)
      if (pageForward.contains(it))
        source.next()
      if (pageBackward.contains(it))
        source.prev()

      if (toggleSlideshow.contains(it)) {
        if (source.timer.isRunning) {
          source.timer.stop()
        } else {
          source.timer.start()
          source.next()
        }
      }

      if (deleteCatalog.contains(it)) {
        source.modifyCatalogFolder(source.index.primary) {
          log.warning("Deleting Folder: $absolutePath")
          deleteRecursively()
        }
      }

      if (archiveCatalog.contains(it)) {
        source.modifyCatalogFolder(source.index.primary) {
          val newPath = File("${ENV.archive}\\$name").toPath()
          log.warning("Moving Folder: $absolutePath --> $newPath")
          if (Files.exists(newPath, LinkOption.NOFOLLOW_LINKS))
            log.severe("Target Path already exists! No action taken!")
          else
            Files.move(toPath(), newPath)
        }
      }

      source.project()
      EventHandler.lock.unlock()
    }
  }
}