package org.fte.glide.utils.extensions

import org.fte.glide.slideshow.CachedImage
import org.fte.glide.slideshow.Geometry
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.BUTTON1
import java.awt.event.MouseEvent.BUTTON2
import java.awt.event.MouseEvent.BUTTON3
import java.text.DecimalFormat


///////////////////////////////////////
// DataStructure Extensions
///////////////////////////////////////


val Array<Geometry>.imageCount: Int
  get() = this.count { it::class == CachedImage::class }

val <K, V> Map<K, V>.reversed: HashMap<V, K>
  get() = HashMap<V, K>().also { newMap -> entries.forEach { newMap[it.value] = it.key } }


///////////////////////////////////////
// Regex Extensions
///////////////////////////////////////

fun Regex.groupValues(input: String): List<String> {
  return this.find(input)!!.groups.map { it!!.value }
}


///////////////////////////////////////
// Primitive Extensions
///////////////////////////////////////

val Long.formattedFileSize: String
  get() {
    if (this <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.00)).toInt()
    return DecimalFormat("#,##0.##").format(this / Math.pow(1024.00, digitGroups.toDouble())) + " " + units[digitGroups]
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


