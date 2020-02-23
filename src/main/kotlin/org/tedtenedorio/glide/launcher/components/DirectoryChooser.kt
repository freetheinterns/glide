package org.tedtenedorio.glide.launcher.components

import org.tedtenedorio.glide.launcher.Launcher
import javax.swing.AbstractButton
import javax.swing.JFileChooser
import javax.swing.JTextField

class DirectoryChooser(
  val banner: JTextField,
  private val parent: Launcher,
  trigger: AbstractButton,
  private val callback: () -> Unit = {}
) : JFileChooser(banner.text) {

  init {
    fileSelectionMode = DIRECTORIES_ONLY
    banner.isEnabled = false
    trigger.addActionListener {
      if (showOpenDialog(parent) == APPROVE_OPTION) {
        banner.text = selectedFile.absolutePath
        callback()
        parent.save()
      }
    }
  }
}