package org.tedtenedorio.glide.listeners

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.retry
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Persist.load
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.concurrent.Executors
import kotlin.system.exitProcess

object EventHandler : KeyEventDispatcher, MouseListener {
  private val log by logger()
  private val context = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private var lastLockedAt = 0L
  var target: Any? = null

  private val PROJECTOR_BINDINGS by lazy {
    println("LOADING PROJECTOR BINDINGS")
    load(ProjectorBindings(), ProjectorBindings.serializer())
  }
  private val LAUNCHER_BINDINGS by lazy {
    println("LOADING LAUNCHER BINDINGS")
    load(LauncherBindings(), LauncherBindings.serializer())
  }

  val lock: Mutex = Mutex()

  fun register() {
    try {
      retry {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this)
      }
    } catch (err: RuntimeException) {
      log.severe("Error registering EventHandler")
      err.printStackTrace()
      exitProcess(1)
    }
  }

  fun deRegister() {
    try {
      retry {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
      }
    } catch (err: RuntimeException) {
      log.severe("Error de-registering EventHandler")
      err.printStackTrace()
      exitProcess(1)
    }
  }

  private fun handleEvent(code: Int) {
    val now = System.currentTimeMillis()
    if (ENV.debounce > now - lastLockedAt) return
    if (!lock.tryLock()) return
    lastLockedAt = now

    when (target) {
      is Projector -> PROJECTOR_BINDINGS.trigger(target as Projector, code)
      is Launcher -> LAUNCHER_BINDINGS.trigger(target as Launcher, code)
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
