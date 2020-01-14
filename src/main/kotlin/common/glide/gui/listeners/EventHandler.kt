package common.glide.gui.listeners

import common.glide.ENV
import common.glide.KEY_BINDINGS
import common.glide.extensions.buttonString
import common.glide.extensions.logger
import common.glide.extensions.string
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
    try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this)
      isRegistered = true
      log.info("EventHandler successfully registered")
    } catch (err: RuntimeException) {
      log.warning("Error registering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  fun deregister() {
    if (!isRegistered) return
    try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
      isRegistered = true
      log.info("EventHandler successfully de-registered")
    } catch (err: RuntimeException) {
      log.warning("Error de-registering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  private fun handleKey(code: KeyEvent) {
    Lock(code.string).throttle(ENV.debounce) {
      log.info(code.string)
      KEY_BINDINGS.trigger(code.keyCode)
    }
  }

  override fun mousePressed(e: MouseEvent) {
    log.info(e.string)
    when (e.buttonString) {
      "Left" -> KEY_BINDINGS.trigger("pageForward")
    }
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
