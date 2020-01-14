package common.glide.extensions

import common.glide.slideshow.CachedImage
import common.glide.slideshow.Geometry
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import java.awt.event.MouseEvent.BUTTON2
import java.awt.event.MouseEvent.BUTTON3
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow


///////////////////////////////////////
// DataStructure Extensions
///////////////////////////////////////

val List<Geometry>.imageCount: Int
  get() = this.count { it::class == CachedImage::class }


///////////////////////////////////////
// Primitive Extensions
///////////////////////////////////////

val Long.formattedFileSize: String
  get() {
    if (this <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.00)).toInt()
    return DecimalFormat("#,##0.##").format(this / 1024.00.pow(digitGroups.toDouble())) + " " + units[digitGroups]
  }


///////////////////////////////////////
// Event Extensions
///////////////////////////////////////

val KeyEvent.action: String
  get() = when (id) {
    KeyEvent.KEY_PRESSED  -> "Press"
    KeyEvent.KEY_RELEASED -> "Lift"
    KeyEvent.KEY_TYPED    -> "Type"
    else                  -> "?"
  }

val KeyEvent.string: String
  get() = "<Key $action: ${KeyEvent.getKeyText(keyCode)} ($keyChar) #$keyCode>"

val MouseEvent.buttonString: String
  get() = when (button) {
    BUTTON1 -> "Left"
    BUTTON2 -> "Middle"
    BUTTON3 -> "Right"
    else    -> "Other"
  }

val MouseEvent.string: String
  get() = "<Click: $buttonString #$button ($x, $y) $component>"


