package org.tedtenedorio.glide.launcher.components

import java.awt.Component
import java.awt.event.ActionListener
import javax.swing.JFileChooser
import javax.swing.JTextField

class DirectoryChooser(
  val banner: JTextField,
  private val parent: Component,
  trigger: (ActionListener) -> Unit
) : JFileChooser(banner.text) {

  init {
    fileSelectionMode = DIRECTORIES_ONLY
    banner.isEnabled = false
    trigger(ActionListener {
      if (showOpenDialog(parent) == APPROVE_OPTION) {
        banner.text = selectedFile.absolutePath
      }
    })
  }
}