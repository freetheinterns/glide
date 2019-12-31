package common.glide.slideshow

import common.glide.utils.extensions.blindObserver
import common.glide.utils.extensions.cache
import common.glide.utils.extensions.string
import kotlin.math.abs

class ImageIndex(
  private val library: Array<Catalog>,
  playlistIndex: Int = 0,
  slideIndex: Int = 0
) : ListIterator<CachedImage>, Comparable<ImageIndex> {
  private var _current: CachedImage? by cache { library[primary][secondary] }
  private var _maxSecondary: Int? by cache { library[primary].size }

  val current: CachedImage
    get() = _current!!
  var primary by blindObserver(playlistIndex) { _maxSecondary = null; _current = null; secondary = 0 }
  var secondary by blindObserver(slideIndex) { _current = null }
  var maxPrimary by cache { library.size }
  val maxSecondary: Int
    get() = _maxSecondary!!
  val copy
    get() = ImageIndex(library, primary, secondary)

  private fun walk(steps: Int): ImageIndex {
    repeat(abs(steps)) {
      secondary += steps / abs(steps)
      if (secondary >= maxSecondary) {
        primary += 1
        if (primary >= maxPrimary)
          primary = 0
      } else if (secondary < 0) {
        primary -= 1
        if (primary < 0)
          primary = maxPrimary - 1
        secondary = maxSecondary - 1
      }
    }
    return this
  }

  private fun iterate(steps: Int): CachedImage {
    secondary += steps
    if (secondary >= maxSecondary) {
      primary += 1
      if (primary >= maxPrimary) {
        primary = maxPrimary - 1
        secondary = maxSecondary - 1
        throw NoSuchElementException("End of slideshow.Catalog")
      }
    } else if (secondary < 0) {
      primary -= 1
      if (primary < 0) {
        primary = 0
        throw NoSuchElementException("End of slideshow.Catalog")
      }
      secondary = maxSecondary - 1
    }
    return current
  }

  fun jump(inc: Int): ImageIndex {
    primary += inc
    if (primary >= maxPrimary) primary = 0
    if (primary < 0) primary = maxPrimary - 1
    return this
  }

  override fun next() = iterate(1)
  override fun previous() = iterate(-1)
  operator fun inc() = this + 1
  operator fun dec() = this - 1
  operator fun plus(inc: Int) = copy.walk(inc)
  operator fun minus(inc: Int) = copy.walk(-inc)
  operator fun plusAssign(inc: Int) {
    walk(inc)
  }

  operator fun minusAssign(inc: Int) {
    walk(-inc)
  }

  override fun hasNext() = primary < maxPrimary - 1 || secondary < maxSecondary - 1
  override fun hasPrevious() = primary > 0 || secondary > 0
  override fun nextIndex() = primary + 1
  override fun previousIndex() = primary - 1

  override fun hashCode() = string.hashCode()
  override fun toString() = "$primary-$secondary-${library.hashCode()}"
  override fun compareTo(other: ImageIndex) =
    when (primary) {
      other.primary -> secondary - other.secondary
      else          -> (primary - other.primary) * 1000
    }

  override fun equals(other: Any?) =
    when (other) {
      is ImageIndex -> primary == other.primary && secondary == other.secondary
      else          -> super.equals(other)
    }
}