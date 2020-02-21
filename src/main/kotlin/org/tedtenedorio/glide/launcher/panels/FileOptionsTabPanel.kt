package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.DirectoryChooser
import javax.swing.JComboBox

class FileOptionsTabPanel(
  listener: Launcher
) : TabPanel("Directories", listener) {
  val root: DirectoryChooser
  val archive: DirectoryChooser
  val ordering: JComboBox<FolderSortStrategy>

  init {
    label("Home")
    description("Where to start looking for playlist folders")
    root = chooser(ENV.root)

    label("Archive")
    description("Where to store archived playlist folder")
    archive = chooser(ENV.archive)

    label("Folder Sort")
    ordering = comboBox(FolderSortStrategy.values(), ENV.ordering)

    spring()
  }
}