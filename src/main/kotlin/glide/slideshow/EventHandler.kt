package glide.slideshow

import glide.async.Lock
import glide.storage.ENV
import glide.storage.KeyBindings
import glide.utils.extensions.buttonString
import glide.utils.extensions.string
import glide.utils.extensions.vprintln
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

object EventHandler : ActionListener, KeyListener, MouseListener {
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

  override fun keyPressed(e: KeyEvent) = handleKey(e)
  override fun keyReleased(e: KeyEvent?) {}
  override fun keyTyped(e: KeyEvent?) {}
  override fun mouseClicked(e: MouseEvent?) {}
  override fun mouseEntered(e: MouseEvent?) {}
  override fun mouseExited(e: MouseEvent?) {}
  override fun mouseReleased(e: MouseEvent?) {}

  // Timer event
  override fun actionPerformed(e: ActionEvent) = KeyBindings.trigger("pageForward")
}
