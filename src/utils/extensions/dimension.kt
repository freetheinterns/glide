package utils.extensions

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

operator fun Dimension.times(scalar: Float): Dimension {
  return Dimension((this.width * scalar).toInt(), (this.height * scalar).toInt())
}

fun Dimension?.equals(other: Any?): Boolean {
  if (other is Dimension) {
    return (this?.width == other.width) && (this.height == other.height)
  }
  return false
}


///////////////////////////////////////
// External Class to Dimension
///////////////////////////////////////

val BufferedImage.dimension: Dimension
  get() = Dimension(this.width, this.height)

val DisplayMode.dimension: Dimension
  get() = Dimension(width, height)