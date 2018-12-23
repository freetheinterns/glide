package slideshow

import async.Lock
import storage.ENV
import utils.extensions.buttonString
import utils.extensions.string
import utils.extensions.vprintln
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

object EventHandler :
  ActionListener,
  KeyListener,
  MouseListener {
  private val actions = hashMapOf(
    "pageForward" to { ENV.projector.next() },
    "pageBackward" to { ENV.projector.prev() },
    "inchForward" to { ENV.projector.dumbNext() },
    "inchBackward" to { ENV.projector.previous() },
    "nextCatalog" to { ENV.projector.nextFolder() },
    "previousCatalog" to { ENV.projector.prevFolder() },
    "deleteCatalog" to { ENV.projector.deleteCurrentDirectory() },
    "archiveCatalog" to { ENV.projector.archiveCurrentDirectory() },
    "toggleSlideshow" to { ENV.projector.toggleTimer() },
    "exit" to { ENV.projector.exit() },
    "changeScaling" to { ENV.projector.scaling = CachedImage.nextScalingOption() }
  )

  private fun takeAction(action: String) = actions[action]!!.invoke()

  var bindings = hashMapOf(
    8 to "inchBackward",
    9 to "nextCatalog",
    16 to "previousCatalog",
    27 to "exit",
    32 to "toggleSlideshow",
    37 to "pageBackward",
    38 to "inchForward",
    39 to "pageForward",
    40 to "inchBackward",
    79 to "changeScaling",
    127 to "deleteCatalog",
    115 to "archiveCatalog"
  )

  private fun handleKey(code: KeyEvent) {
    if (Lock(code.string).throttle(ENV.debounce)) return
    vprintln(code.string)

    bindings[code.keyCode]?.let(::takeAction)
  }

  override fun mousePressed(e: MouseEvent) {
    vprintln(e.string)
    when (e.buttonString) {
      "Left"   -> takeAction("pageForward")
      "Right"  -> {
      }
      "Middle" -> {
      }
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
  override fun actionPerformed(e: ActionEvent) = takeAction("pageForward")
}
