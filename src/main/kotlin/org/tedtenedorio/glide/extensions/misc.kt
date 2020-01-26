package org.tedtenedorio.glide.extensions

import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import org.tedtenedorio.glide.slideshow.geometry.Geometry
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

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
  var sum = 0L
  for (element in this) {
    sum += selector(element)
  }
  return sum
}
