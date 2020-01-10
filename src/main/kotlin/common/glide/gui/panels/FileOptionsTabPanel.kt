package common.glide.gui.panels

import common.glide.gui.Launcher
import common.glide.gui.components.DirectoryChooser
import common.glide.slideshow.FolderSortStrategy
import common.glide.storage.ENV
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