package slideshow

import utils.extensions.blindObserver
import utils.extensions.lazyCache
import utils.extensions.string

class ImageIndex(
        private val library: List<Playlist>
) : ListIterator<CachedImage>, Comparable<ImageIndex> {
  var current: CachedImage? by lazyCache { library[primary][secondary] }
  var maxSecondary: Int?         by lazyCache { library[primary].size }
  var secondary by blindObserver(0) { current = null }
  var maxPrimary by lazyCache { library.size }
  var primary by blindObserver(0) { maxSecondary = null; current = null; secondary = 0 }

  private val copy: ImageIndex
    get() {
      val cpy = ImageIndex(library)
      cpy.primary = primary
      cpy.secondary = secondary
      return cpy
    }

  override fun next(): CachedImage {
    var nextPrimary = primary
    secondary += 1
    if (secondary >= maxSecondary!!) {
      nextPrimary += 1
      if (nextPrimary >= maxPrimary) throw NoSuchElementException("End of slideshow.Playlist")
    }
    primary = nextPrimary
    return current!!
  }

  override fun previous(): CachedImage {
    var nextPrimary = primary
    secondary -= 1
    if (secondary < 0) {
      nextPrimary -= 1
      if (nextPrimary < 0) throw NoSuchElementException("End of slideshow.Playlist")
      primary = nextPrimary
      secondary = maxSecondary!! - 1
      return current!!
    }
    primary = nextPrimary
    return current!!
  }

  operator fun inc(): ImageIndex {
    next(); return this
  }

  operator fun dec(): ImageIndex {
    previous(); return this
  }

  operator fun plus(inc: Int) = copy.increment(inc)
  operator fun minus(inc: Int) = copy.decrement(inc)


  override fun hasNext() = primary < maxPrimary - 1 || secondary < maxSecondary!! - 1
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
      else                    -> super.equals(other)
    }

  fun increment(inc: Int): ImageIndex {
    repeat(inc, {
      try {
        next()
      } catch (ex: NoSuchElementException) {
        primary = 0
      }
    })
    return this
  }

  fun decrement(inc: Int): ImageIndex {
    repeat(inc, {
      try {
        previous()
      } catch (ex: NoSuchElementException) {
        setToLastPosition()
      }
    })
    return this
  }

  private fun setToLastPosition() {
    primary = maxPrimary - 1; secondary = maxSecondary!! - 1
  }

  fun jump(inc: Int): ImageIndex {
    primary += inc
    if (primary >= maxPrimary) primary = 0
    if (primary < 0) primary = maxPrimary - 1
    return this
  }
}