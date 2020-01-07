package common.glide.slideshow

import common.glide.storage.ENV
import common.glide.utils.extensions.createdAt
import common.glide.utils.extensions.formattedFileSize
import common.glide.utils.extensions.imageCount
import java.awt.BasicStroke
import java.awt.BasicStroke.CAP_ROUND
import java.awt.BasicStroke.JOIN_ROUND
import java.awt.Color.BLACK
import java.awt.Color.WHITE
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.text.SimpleDateFormat

class MarginPanel(private val app: Projector) : Geometry {
  private var lines = arrayListOf<String>()
  private val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
  private val font = Font(ENV.fontName, Font.PLAIN, 22)
  private val stroke = BasicStroke(4.0f, CAP_ROUND, JOIN_ROUND)
  override fun build(xOffset: Int, yOffset: Int): Geometry {
    return this
  }

  override fun paint(g: Graphics?) {
    val g2: Graphics2D = g as Graphics2D
    g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
    g2.font = font
    g2.stroke = stroke
    g2.translate(10, 10)

    val frc = g2.fontRenderContext
    val currentPlaylist = app.library[app.index.primary]
    lines = arrayListOf()

    addFolderName()

    lines.add(currentPlaylist.folderSize.formattedFileSize)
    lines.add(formatter.format(currentPlaylist.file.createdAt))
    lines.add("")
    lines.add("scale: ${CachedImage.SCALING_OPTIONS[ENV.scaling]}")

    addFileName()
    addFileCount()
    addFolderCount()

    lines.forEach {
      g2.translate(0, 25)
      if (it.isBlank()) return@forEach
      val textOutline = font.createGlyphVector(frc, it).outline
      g2.color = BLACK
      g2.draw(textOutline)
      g2.color = WHITE
      g2.fill(textOutline)
    }
  }

  private fun addFolderName() {
    if (!ENV.showMarginFolderName) return
    lines.add(app.index.current.file.parentFile.name)
  }

  private fun addFileName() {
    if (!ENV.showMarginFileName) return
    var currentLine = app.index.current.file.name
    repeat(app.geometry.imageCount - 1) {
      currentLine += " & ${(app.index + (it + 1)).current.file.name}"
    }

    lines.add(currentLine)
  }

  private fun addFileCount() {
    if (!ENV.showMarginFileCount) return
    var currentLine = "${app.index.secondary + 1}"
    repeat(app.geometry.imageCount - 1) {
      currentLine += " & ${app.index.secondary + 2 + it}"
    }

    lines.add("$currentLine / ${app.index.maxSecondary}")
  }

  private fun addFolderCount() {
    if (!ENV.showMarginFolderCount) return
    lines.add("${app.index.primary + 1} / ${app.index.maxPrimary}")
  }
}