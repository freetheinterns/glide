package org.tedtenedorio.glide.gui.listeners

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
  override val version: Int = 0
  @Transient override var serializer = serializer()

  fun trigger(source: Projector, it: Int) {
    thread(
      isDaemon = true,
      priority = 2,
      name = "ProjectorEvent-$it"
    ) {
      if (archiveCatalog.contains(it))
        source.archiveCurrentDirectory()
      if (deleteCatalog.contains(it))
        source.deleteCurrentDirectory()
      if (exit.contains(it))
        quit(0)
      if (inchBackward.contains(it))
        source.previous()
      if (inchForward.contains(it))
        source.dumbNext()
      if (nextCatalog.contains(it))
        source.nextFolder()
      if (pageBackward.contains(it))
        source.prev()
      if (pageForward.contains(it))
        source.next()
      if (previousCatalog.contains(it))
        source.prevFolder()
      if (toggleSlideshow.contains(it))
        source.toggleTimer()
      EventHandler.lock.unlock()
    }
  }
}