package org.tedtenedorio.glide.gui.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.gui.Launcher
import org.tedtenedorio.glide.gui.components.DirectoryChooser
import javax.swing.JComboBox

class FileOptionsTabPanel(
  totalHeight: Int,
  listener: Launcher
) : TabPanel("Directories", totalHeight, listener) {
  val root: DirectoryChooser = buildChooser(
    name = "Home",
    home = ENV.root,
    description = "Where to start looking for playlist folders"
  )
  val archive: DirectoryChooser = buildChooser(
    name = "Archive",
    home = ENV.archive,
    description = "Where to store archived playlist folders"
  )

  val ordering: JComboBox<FolderSortStrategy> = buildComboBox(
    name = "Folder Sort",
    options = FolderSortStrategy.values(),
    selected = ENV.ordering
  )
}