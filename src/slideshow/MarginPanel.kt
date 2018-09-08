package slideshow

import storage.ENV
import utils.extensions.createdAt
import utils.extensions.formattedFileSize
import utils.inheritors.Geometry
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.text.SimpleDateFormat

class MarginPanel(private val app: Projector) : Geometry {
  private var lines = arrayListOf<String>()
  override val geometryType = "slideshow.MarginPanel"
  private val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")
  private val font = Font(ENV.fontName, Font.PLAIN, 16)
  override fun build(xOffset: Int, yOffset: Int): Geometry {
    return this
  }

  override fun paint(g: Graphics?) {
    g?.color = Color.red
    g?.font = font
    val currentPlaylist = app.library!![app.index.primary]
    lines = arrayListOf()

    addFolderName()

    lines.add(currentPlaylist.folderSize.formattedFileSize)
    lines.add(formatter.format(currentPlaylist.file.createdAt))
    lines.add("")
    lines.add("${ENV.scaling}")

    addFileName()
    addFileCount()
    addFolderCount()

    lines.forEachIndexed { index, it -> g?.drawString(it, 5, 20 + index * 20) }
  }

  private fun addFolderName() {
    if (!ENV.showMarginFolderName) return
    lines.add(app.index.current!!.file.parentFile.name)
  }

  private fun addFileName() {
    if (!ENV.showMarginFileName) return
    var currentLine = app.index.current!!.file.name
    repeat(app.imageGeometryCount - 1) { idx ->
      currentLine += " & ${(app.index + (idx + 1)).current!!.file.name}"
    }

    lines.add(currentLine)
  }

  private fun addFileCount() {
    if (!ENV.showMarginFileCount) return
    var currentLine = "${app.index.secondary + 1}"
    repeat(app.imageGeometryCount - 1) { idx ->
      currentLine += " & ${app.index.secondary + 2 + idx}"
    }

    lines.add("$currentLine / ${app.index.maxSecondary}")
  }

  private fun addFolderCount() {
    if (!ENV.showMarginFolderCount) return
    lines.add("${app.index.primary + 1} / ${app.index.maxPrimary}")
  }
}