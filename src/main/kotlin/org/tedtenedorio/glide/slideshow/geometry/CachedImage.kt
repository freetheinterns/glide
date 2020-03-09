package org.tedtenedorio.glide.slideshow.geometry

import org.tedtenedorio.glide.BLACKHOLE
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.FRAME_RENDER_PRIORITY
import org.tedtenedorio.glide.extensions.PROJECTOR_WINDOW_SIZE
import org.tedtenedorio.glide.extensions.bufferedImage
import org.tedtenedorio.glide.extensions.scaleToFit
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.utils.createOutlinedTypeSetter
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import kotlin.properties.Delegates.observable

class CachedImage(val file: File) : Geometry {
  val byteSize: Long by lazy { file.length() }
  override var position: Dimension by observable(Dimension()) { _, _, _ ->
    priority = FRAME_RENDER_PRIORITY
    BLACKHOLE.consume(width) // Ensures the buffered image has been loaded and rendered
  }

  var priority: Int = 0

  val path: String by lazy { file.absolutePath }
  val width: Int by lazy { sizedImage.width }
  val height: Int by lazy { sizedImage.height }
  val name: String by lazy { file.name }

  private val sizedImage: BufferedImage by path.cache({
    weigher = { _, _ -> byteSize.toInt() }
    maximumWeight = ENV.cacheSizeBytes
  }) {
    priority = FRAME_RENDER_PRIORITY
    file.bufferedImage.scaleToFit(PROJECTOR_WINDOW_SIZE)
  }

  override fun paint(g: Graphics2D) {
    g.translate(position.width, position.height)
    g.drawRenderedImage(sizedImage, null)

    if (!ENV.showFooterFileNumber) return

    val drawOutlinedText = g.createOutlinedTypeSetter()
    g.translate(5, sizedImage.height - 10)
    g.drawOutlinedText(file.nameWithoutExtension)
  }

  override fun toString(): String = StringBuilder("CachedImage(").apply {
    append("file=$name")
    append(")")
  }.toString()
}



