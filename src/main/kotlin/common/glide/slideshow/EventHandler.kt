package common.glide.slideshow

import common.glide.async.Lock
import common.glide.storage.ENV
import common.glide.storage.KeyBindings
import common.glide.utils.extensions.buttonString
import common.glide.utils.extensions.logger
import common.glide.utils.extensions.string
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

object EventHandler : KeyEventDispatcher, MouseListener {
  private var isRegistered = false

  fun register() {
    if (isRegistered) return
    try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this)
      isRegistered = true
      logger.info("EventHandler successfully registered")
    } catch (err: RuntimeException) {
      logger.warning("Error registering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  fun deregister() {
    if (!isRegistered) return
    try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
      isRegistered = true
      logger.info("EventHandler successfully de-registered")
    } catch (err: RuntimeException) {
      logger.warning("Error de-registering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  private fun handleKey(code: KeyEvent) {
    Lock(code.string).throttle(ENV.debounce) {
      logger.info(code.string)
      KeyBindings.triggerByCode(code.keyCode)
    }
  }

  override fun mousePressed(e: MouseEvent) {
    logger.info(e.string)
    when (e.buttonString) {
      "Left" -> KeyBindings.trigger("pageForward")
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
