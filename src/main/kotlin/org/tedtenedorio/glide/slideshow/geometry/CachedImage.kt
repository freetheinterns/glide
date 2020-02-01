package org.tedtenedorio.glide.slideshow.geometry

import org.tedtenedorio.glide.BLACKHOLE
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.bufferedImage
import org.tedtenedorio.glide.extensions.scaleToFit
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.properties.CachedProperty.Companion.invalidate
import org.tedtenedorio.glide.slideshow.Projector
import org.tedtenedorio.glide.storage.Cacheable
import org.tedtenedorio.glide.utils.createOutlinedTypeSetter
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import kotlin.properties.Delegates.observable

class CachedImage(val file: File) : Geometry, Cacheable {
  override val byteSize: Long by lazy(file::length)
  override var position: Dimension by observable(Dimension()) { _, _, _ ->
    priority = Projector.singleton?.frameCount ?: priority
    BLACKHOLE.consume(width)
  }

  override var priority: Int = 0

  private val sizedImage: BufferedImage by cache {
    memoize()
    priority = Projector.singleton?.frameCount ?: priority
    Projector.singleton?.size?.let(file.bufferedImage::scaleToFit) ?: file.bufferedImage
  }

  val path: String by lazy(file::getAbsolutePath)
  val width: Int by lazy(sizedImage::getWidth)
  val height: Int by lazy(sizedImage::getHeight)
  val name: String by lazy(file::getName)

  override fun clear() {
    ::sizedImage.invalidate(this)
  }

  override fun paint(g: Graphics2D) {
    g.translate(position.width, position.height)
    g.drawRenderedImage(sizedImage, null)

    if (!ENV.showFooterFileNumber) return

    val drawOutlinedText = g.createOutlinedTypeSetter()
    g.translate(5, sizedImage.height - 10)
    g.drawOutlinedText(file.nameWithoutExtension)
  }
}



