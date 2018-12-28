package glide.slideshow

import glide.async.Lock
import glide.storage.ENV
import glide.storage.KeyBindings
import glide.utils.extensions.buttonString
import glide.utils.extensions.string
import glide.utils.extensions.vprintln
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
      vprintln("EventHandler successfully registered")
    } catch (err: RuntimeException) {
      vprintln("Error registering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  fun deregister() {
    if (!isRegistered) return
    try {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this)
      isRegistered = true
      vprintln("EventHandler successfully deregistered")
    } catch (err: RuntimeException) {
      vprintln("Error deregistering EventHandler")
      err.printStackTrace()
      isRegistered = false
    }
  }

  private fun handleKey(code: KeyEvent) {
    if (Lock(code.string).throttle(ENV.debounce)) return
    vprintln(code.string)
    KeyBindings.triggerByCode(code.keyCode)
  }

  override fun mousePressed(e: MouseEvent) {
    vprintln(e.string)
    when (e.buttonString) {
      "Left" -> KeyBindings.trigger("pageForward")
    }
  }

  override fun dispatchKeyEvent(e: KeyEvent): Boolean {
    if (e.id == KeyEvent.KEY_PRESSED)
      handleKey(e)
    return false
  }
  override fun mouseClicked(e: MouseEvent?) {}
  override fun mouseEntered(e: MouseEvent?) {}
  override fun mouseExited(e: MouseEvent?) {}
  override fun mouseReleased(e: MouseEvent?) {}
}
