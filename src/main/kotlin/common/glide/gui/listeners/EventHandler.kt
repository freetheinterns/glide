package common.glide.gui.listeners

import common.glide.ENV
import common.glide.LAUNCHER_BINDINGS
import common.glide.PROJECTOR_BINDINGS
import common.glide.extensions.logger
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

object EventHandler : KeyEventDispatcher, MouseListener {
  private val log by logger()
  private var isRegistered = false

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

  private fun handleKey(code: KeyEvent) {
    Lock(code.keyCode).throttle(ENV.debounce) {
      ENV.projector?.let { PROJECTOR_BINDINGS.trigger(it, code.keyCode) }
      ENV.launcher?.let { LAUNCHER_BINDINGS.trigger(it, code.keyCode) }
    }
  }

  override fun mousePressed(e: MouseEvent) {
    ENV.projector?.let { PROJECTOR_BINDINGS.trigger(it, -e.button) }
    ENV.launcher?.let { LAUNCHER_BINDINGS.trigger(it, -e.button) }
  }

  override fun dispatchKeyEvent(e: KeyEvent) = false.also {
    if (e.id == KeyEvent.KEY_PRESSED)
      handleKey(e)
  }

  override fun mouseClicked(e: MouseEvent?) {}
  override fun mouseEntered(e: MouseEvent?) {}
  override fun mouseExited(e: MouseEvent?) {}
  override fun mouseReleased(e: MouseEvent?) {}
}
