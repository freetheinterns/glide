package slideshow

import storage.ENV
import utils.extensions.CACHE_RESIZED_IMAGE
import utils.extensions.shouldThrottle
import utils.extensions.string
import utils.extensions.vprintln
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class EventHandler(private val app: Projector) :
  ActionListener,
  KeyListener,
  MouseListener,
  WindowAdapter() {

  private val leftMouseButton = MouseEvent.BUTTON1
  private val middleMouseButton = MouseEvent.BUTTON2
  private val rightMouseButton = MouseEvent.BUTTON3

  private val backspaceAction = { app.previous() }
  private val deleteAction = { app.deleteCurrentDirectory() }
  private val downArrowAction = { app.previous() }
  private val escapeAction = { app.exit() }
  private val f4Action = { app.archiveCurrentDirectory() }
  private val leftArrowAction = { app.prev() }
  private val rightArrowAction = { app.next() }
  private val shiftAction = { app.prevFolder() }
  private val spaceAction = { app.toggleTimer() }
  private val tabAction = { app.nextFolder() }
  private val upArrowAction = { app.dumbNext() }
  private val sbutton = {
    if (ENV.scaling == Image.SCALE_AREA_AVERAGING) {
      ENV.scaling = Image.SCALE_DEFAULT
    } else {
      ENV.scaling *= 2
    }
    app.focus!!.sizedImage = null
    app.focus!!.cacheLevel = CACHE_RESIZED_IMAGE
    val next = (app.index + 1).current
    next?.sizedImage = null
    next?.cacheLevel = CACHE_RESIZED_IMAGE
    app.focus = null
    app.updateCaching()
  }

  private fun handleKey(code: KeyEvent) {
    if (shouldThrottle("ks-${code.keyCode}", ENV.debounce)) return
    vprintln(code.string)

    when (code.keyCode) {
      8   -> backspaceAction()
      9   -> tabAction()
      16  -> shiftAction()
      27  -> escapeAction()
      32  -> spaceAction()
      37  -> leftArrowAction()
      38  -> upArrowAction()
      39  -> rightArrowAction()
      40  -> downArrowAction()
      79  -> sbutton()
      127 -> deleteAction()
      115 -> f4Action()
    }
  }

  override fun mousePressed(e: MouseEvent) {
    vprintln(e.string)
    when (e.button) {
      leftMouseButton -> app.next()
      //      rightMouseButton  -> app.menu.show(e.component, e.x, e.y)
      //      middleMouseButton -> app.menu.show(e.component, e.x, e.y)
    }
  }

  override fun keyPressed(e: KeyEvent) = handleKey(e)
  override fun keyReleased(e: KeyEvent?) {}
  override fun keyTyped(e: KeyEvent?) {}
  override fun mouseClicked(e: MouseEvent?) {}
  override fun mouseEntered(e: MouseEvent?) {}
  override fun mouseExited(e: MouseEvent?) {}
  override fun mouseReleased(e: MouseEvent?) {}
  override fun windowClosed(e: WindowEvent?) = app.exit()

  // Timer event
  override fun actionPerformed(e: ActionEvent) = app.next()
}
