package org.fte.glide.gui.components

import org.fte.glide.gui.panels.TabPanel
import java.awt.Component
import javax.swing.JButton
import javax.swing.JFileChooser

class DirectoryChooser(
        home: String,
        private val parent: Component,
        buttonText: String = "Select Folder"
) : JFileChooser(home) {
  val banner = TabPanel.TextField(home)
  val trigger = JButton(buttonText)

  init {
    fileSelectionMode = DIRECTORIES_ONLY
    banner.alignmentX = Component.LEFT_ALIGNMENT
    banner.isEnabled = false
    trigger.alignmentX = Component.LEFT_ALIGNMENT
    trigger.addActionListener {
      if (showOpenDialog(parent) == APPROVE_OPTION) {
        banner.text = selectedFile.absolutePath
      }
    }
  }
}