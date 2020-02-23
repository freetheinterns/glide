package org.tedtenedorio.glide.slideshow

import org.tedtenedorio.glide.extensions.catalogs
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.properties.CachedProperty.Companion.invalidate
import org.tedtenedorio.glide.properties.ChangeTriggeringProperty.Companion.blindObserver
import org.tedtenedorio.glide.slideshow.geometry.CachedImage
import java.io.File
import kotlin.math.abs


class Library(
  root: String
) : Iterable<Catalog> {
  private var catalogs: List<Catalog> =
    File(root).catalogs.toMutableList()
  val isEmpty
    get() = catalogs.sumBy(Catalog::size) == 0
  val size
    get() = catalogs.size

  operator fun get(index: Int): Catalog = catalogs[index]

  fun filter(predicate: (Catalog) -> Boolean) {
    catalogs = catalogs.filter(predicate)
  }

  override fun iterator(): Iterator<Catalog> = catalogs.iterator()

  inner class Index(
    playlistIndex: Int = 0,
    slideIndex: Int = 0
  ) : ListIterator<CachedImage>, Comparable<Index> {
    private val uuid: Int = java.util.UUID.randomUUID().hashCode()
    val current: CachedImage by cache { this@Library[primary][secondary] }
    var primary: Int by blindObserver(playlistIndex) {
      ::maxSecondary.invalidate(this)
      ::current.invalidate(this)
      secondary = 0
    }
    var secondary: Int by blindObserver(slideIndex) { ::current.invalidate(this) }
    val maxPrimary: Int by lazy { this@Library.size }
    val maxSecondary: Int by cache { this@Library[primary].size }
    val copy
      get() = Index(primary, secondary)

    private fun walk(steps: Int): Index {
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

    fun jump(inc: Int): Index {
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
    override fun hasNext() = primary < maxPrimary - 1 || secondary < maxSecondary - 1
    override fun hasPrevious() = primary > 0 || secondary > 0
    override fun nextIndex() = primary + 1
    override fun previousIndex() = primary - 1
    override fun hashCode(): Int = uuid
    override fun toString() = "Index: primary= $primary/$maxPrimary secondary= $secondary/$maxSecondary"

    operator fun plusAssign(inc: Int) {
      walk(inc)
    }

    operator fun minusAssign(inc: Int) {
      walk(-inc)
    }

    override fun compareTo(other: Index) =
      when (primary) {
        other.primary -> secondary - other.secondary
        else -> (primary - other.primary) * 1000
      }

    override fun equals(other: Any?) =
      when (other) {
        is Index -> primary == other.primary && secondary == other.secondary
        else -> super.equals(other)
      }
  }
}