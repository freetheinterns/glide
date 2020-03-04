package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.formattedFileSize
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.logger
import org.tedtenedorio.glide.extensions.mix
import org.tedtenedorio.glide.extensions.perpendicularBox
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.extensions.trace
import org.tedtenedorio.glide.launcher.panels.LibraryEditorPanel
import org.tedtenedorio.glide.slideshow.Catalog
import org.tedtenedorio.glide.slideshow.Library
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Insets
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.border.LineBorder

class LibraryEditor : Box(BoxLayout.Y_AXIS) {
  val library: Library = Library(ENV.root)
  private val smallFileSize = library
    .minBy(Catalog::folderSize)
    ?.folderSize
    ?.coerceAtLeast(1_000_000)
    ?: 1_000_000
  private val largeFileSize = (
    library
      .maxBy(Catalog::folderSize)
      ?.folderSize
      ?.coerceAtLeast(10_000_000)
      ?: 10_000_000
    ).toDouble() - smallFileSize

  inner class CatalogBox(
    catalog: Catalog
  ) : Box(BoxLayout.X_AXIS) {
    init {
      isOpaque = true
      border = LineBorder(ENV.darkSelected, 1)

      gap(20)
      add(
        JLabel(
          catalog.file.name.substring(
            0,
            catalog.file.name.length.coerceAtMost(27)
          )
        ),
        Component.CENTER_ALIGNMENT
      )
      gap(10)
      spring()
      gap(10)
      add(JLabel("Count: ${catalog.size}"), Component.CENTER_ALIGNMENT)
      gap(10)
      spring()
      gap(10)
      add(
        JLabel(catalog.folderSize.formattedFileSize).also {
          it.foreground = Color.RED.mix(
            Color.GREEN,
            (catalog.folderSize - smallFileSize) / largeFileSize
          )
        },
        Component.CENTER_ALIGNMENT
      )
      gap(30)

      maximumSize = Dimension(LibraryEditorPanel.HARD_WIDTH, CATALOG_HEIGHT)
      minimumSize = Dimension(minimumSize.width, CATALOG_HEIGHT)
      preferredSize = Dimension(preferredSize.width, CATALOG_HEIGHT)
    }
  }

  init {
    isOpaque = true
    background = ENV.dark
    log.trace { "Largest File: " + largeFileSize.toLong().formattedFileSize }
    log.trace { "Smallest File: " + smallFileSize.formattedFileSize }
    log.trace { "Library Size: ${library.size}" }
    library.forEach {
      gap(CATALOG_INSETS.top)
      perpendicularBox {
        gap(CATALOG_INSETS.left)
        add(CatalogBox(it))
        gap(CATALOG_INSETS.right)
        preferredSize = Dimension(preferredSize.width, CATALOG_HEIGHT)
        minimumSize = Dimension(minimumSize.width, CATALOG_HEIGHT)
        maximumSize = Dimension(maximumSize.width, CATALOG_HEIGHT)
      }
    }
    gap(CATALOG_INSETS.bottom)
  }

  companion object {
    private val CATALOG_INSETS: Insets = Insets(10, 10, 10, 10)
    private const val CATALOG_HEIGHT = 35
    private val log by logger()
  }
}