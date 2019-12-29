package common.glide.slideshow

import common.glide.storage.ENV
import common.glide.utils.extensions.createdAt
import common.glide.utils.extensions.formattedFileSize
import common.glide.utils.extensions.imageCount
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.text.SimpleDateFormat

class MarginPanel(private val app: Projector) : Geometry {
  private var lines = arrayListOf<String>()
  private val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
  private val font = Font(ENV.fontName, Font.PLAIN, 16)
  override fun build(xOffset: Int, yOffset: Int): Geometry {
    return this
  }

  override fun paint(g: Graphics?) {
    g?.color = Color.red
    g?.font = font
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

    lines.forEachIndexed { index, it -> g?.drawString(it, 5, 20 + index * 20) }
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