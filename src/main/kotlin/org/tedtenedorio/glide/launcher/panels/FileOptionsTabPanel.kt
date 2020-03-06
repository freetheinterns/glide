package org.tedtenedorio.glide.launcher.panels

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.EVENT_DISPATCHER
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.DirectoryChooser
import javax.swing.JComboBox

class FileOptionsTabPanel(
  listener: Launcher
) : TabPanel("Directories", listener) {
  lateinit var root: DirectoryChooser
  lateinit var archive: DirectoryChooser
  val ordering: JComboBox<FolderSortStrategy>

  init {
    label("Home")
    description("Where to start looking for playlist folders")
    root = chooser(ENV.root) {
      archive.banner.text = root.banner.text + "\\archive"
    }

    label("Archive")
    description("Where to store archived playlist folder")
    archive = chooser(ENV.archive)

    label("Folder Sort")
    ordering = comboBox(FolderSortStrategy.values(), ENV.ordering)
    button("Reverse").addActionListener {
      GlobalScope.launch(EVENT_DISPATCHER) {
        listener.libraryEditorPanel.safeLibrary?.reverse()
        listener.libraryEditorPanel.repaintWindow()
      }
    }

    spring()
  }
}