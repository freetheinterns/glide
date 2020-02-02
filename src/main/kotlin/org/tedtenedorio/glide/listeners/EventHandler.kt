package org.tedtenedorio.glide.listeners

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.LAUNCHER_BINDINGS
import org.tedtenedorio.glide.PROJECTOR_BINDINGS
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.slideshow.Projector
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.concurrent.Executors

object EventHandler : KeyEventDispatcher, MouseListener {
  private val log by logger()
  private val context = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private var isRegistered = false
  private var lastLockedAt = 0L

  val lock: Mutex = Mutex()

  fun register() {
    if (isRegistered) return
    isRegistered = try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this)
      true
    } catch (err: RuntimeException) {
      log.warning("Error registering EventHandler")
      err.printStackTrace()
      false
    }
  }

  fun deRegister() {
    if (!isRegistered) return
    isRegistered = try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
      false
    } catch (err: RuntimeException) {
      log.warning("Error de-registering EventHandler")
      err.printStackTrace()
      true
    }
  }

  private fun handleEvent(code: Int) {
    val now = System.currentTimeMillis()
    if (ENV.debounce > now - lastLockedAt) return
    if (!lock.tryLock()) return
    lastLockedAt = now

    val projector = Projector.singleton
    val launcher = Launcher.singleton

    when {
      projector != null -> PROJECTOR_BINDINGS.trigger(projector, code)
      launcher != null -> LAUNCHER_BINDINGS.trigger(launcher, code)
      else -> lock.unlock()
    }
  }

  override fun mousePressed(e: MouseEvent) {
    runBlocking {
      withContext(context + CoroutineName("GlideMouseEventHandler")) {
        handleEvent(-e.button)
      }
    }
  }

  override fun dispatchKeyEvent(e: KeyEvent) = false.also {
    runBlocking {
      withContext(context + CoroutineName("GlideKeyEventHandler")) {
        if (e.id == KeyEvent.KEY_PRESSED) {
          handleEvent(e.keyCode)
        }
      }
    }
  }

  override fun mouseClicked(e: MouseEvent?) {}
  override fun mouseEntered(e: MouseEvent?) {}
  override fun mouseExited(e: MouseEvent?) {}
  override fun mouseReleased(e: MouseEvent?) {}
}
