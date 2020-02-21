package org.tedtenedorio.glide.launcher.components

import java.awt.Component
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTextField

class DirectoryChooser(
  val banner: JTextField,
  trigger: JButton,
  private val parent: Component
) : JFileChooser(banner.text) {

  init {
    alignmentX = Component.LEFT_ALIGNMENT
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