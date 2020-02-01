package org.tedtenedorio.glide.extensions

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import java.awt.Dimension
import java.awt.DisplayMode
import java.awt.image.BufferedImage
import kotlin.math.min


///////////////////////////////////////
// Operator Definitions
///////////////////////////////////////

operator fun Dimension.div(other: Dimension): Float {
  return min(this.width / other.width.toFloat(), this.height / other.height.toFloat())
}

operator fun Dimension.times(scalar: Float): Dimension =
  Dimension((this.width * scalar).toInt(), (this.height * scalar).toInt())

operator fun Dimension.times(scalar: Int): Dimension =
  Dimension(this.width * scalar, this.height * scalar)

fun Dimension?.equals(other: Any?): Boolean {
  if (other is Dimension) {
    return (this?.width == other.width) && (this.height == other.height)
  }
  return false
}


///////////////////////////////////////
// Fitting Logic
///////////////////////////////////////

fun Dimension.fitCentered(pages: List<CachedImage>): List<CachedImage> {
  var margin = (size.width - pages.sumBy(CachedImage::width)) / 2

  return pages
    .run { if (ENV.direction) reversed() else this }
    .map {
      it.position = Dimension(margin, (size.height - it.height) / 2)
      margin += it.width
      it
    }
}


///////////////////////////////////////
// External Class to Dimension
///////////////////////////////////////

val BufferedImage.dimension: Dimension
  get() = Dimension(this.width, this.height)

val DisplayMode.dimension: Dimension
  get() = Dimension(width, height)