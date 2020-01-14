package common.glide.slideshow

import common.glide.ENV
import common.glide.extensions.createdAt
import common.glide.extensions.formattedFileSize
import common.glide.extensions.imageCount
import common.glide.utils.createOutlinedTypeSetter
import java.awt.Graphics2D
import java.text.SimpleDateFormat

class MarginPanel(private val app: Projector) : Geometry {
  companion object {
    private val DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
  }

  override fun build(xOffset: Int, yOffset: Int): Geometry = this

  override fun paint(g: Graphics2D) {
    g.translate(10, 10)

    val drawOutlinedText = g.createOutlinedTypeSetter(
      fontName = ENV.fontName,
      fontSize = 22
    )

    displayLines.forEach {
      g.translate(0, 25)
      if (it.isBlank()) return@forEach
      g.drawOutlinedText(it)
    }
  }

  private val folderName: String?
    get() = if (!ENV.showMarginFolderName) {
      null
    } else {
      app.index.current.file.parentFile.name
    }

  private val fileName: String?
    get() = if (!ENV.showMarginFileName) {
      null
    } else {
      List(app.geometry.imageCount) {
        (app.index + it).current.file.name
      }.joinToString(" & ")
    }

  private val fileCount: String?
    get() = if (!ENV.showMarginFileCount) {
      null
    } else {
      List(app.geometry.imageCount) {
        (app.index.secondary + 1 + it).toString()
      }.joinToString(" & ") + " / ${app.index.maxSecondary}"
    }

  private val folderCount: String?
    get() = if (!ENV.showMarginFolderCount) {
      null
    } else {
      "${app.index.primary + 1} / ${app.index.maxPrimary}"
    }

  private val displayLines: List<String>
    get() {
      val lines = mutableListOf<String>()
      val currentPlaylist = app.library[app.index.primary]

      folderName?.let(lines::add)

      lines.add(currentPlaylist.folderSize.formattedFileSize)
      lines.add(DATE_FORMAT.format(currentPlaylist.file.createdAt))
      lines.add("")

      fileName?.let(lines::add)
      fileCount?.let(lines::add)
      folderCount?.let(lines::add)

      return lines
    }
}