package org.fte.glide.gui.panels

import org.fte.glide.gui.Launcher
import org.fte.glide.gui.components.DirectoryChooser
import org.fte.glide.storage.ENV
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

  val ordering: JComboBox<String> = buildComboBox(
    name = "Folder Sort",
    options = ENV.ORDER_ENUMS,
    selected = ENV.ordering
  )
}