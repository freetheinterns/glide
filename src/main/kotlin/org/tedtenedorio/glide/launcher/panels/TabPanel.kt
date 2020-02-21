package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.box
import org.tedtenedorio.glide.extensions.derive
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.DirectoryChooser
import org.tedtenedorio.glide.launcher.components.LabelButton
import org.tedtenedorio.glide.launcher.components.Slider
import java.awt.BasicStroke
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.border.EmptyBorder
import kotlin.system.exitProcess

abstract class TabPanel(
  val title: String,
  private val listener: Launcher
) : Box(BoxLayout.Y_AXIS) {
  var highlighted = false
  val label = LabelButton {
    title = this@TabPanel.title
    listener = this@TabPanel.listener
  }

  private val items = mutableListOf<Component>()
  private val closeButton: LabelButton
  private val header: JLabel

  init {
    isOpaque = true
    border = EmptyBorder(0, 20, 0, 0)

    // Set overall dimensions
    preferredSize = Dimension(HARD_WIDTH, Launcher.HARD_HEIGHT)
    minimumSize = Dimension(HARD_WIDTH, Launcher.HARD_HEIGHT)
    maximumSize = Dimension(HARD_WIDTH, Launcher.HARD_HEIGHT)
    background = ENV.background

    // Close Button
    closeButton = LabelButton {
      listener = ActionListener { exitProcess(0) }
      background = ENV.darkSelected
      hoverColor = ENV.exitRed
      preferredSize = Dimension(56, 38)
      minimumSize = Dimension(56, 38)
      maximumSize = Dimension(56, 38)
      alignmentY = Component.TOP_ALIGNMENT

      paint = { g ->
        g.color = ENV.foreground
        g.stroke = BasicStroke(1F)
        val l = 11
        g.drawLine(22, 13, 22 + l, 13 + l)
        g.drawLine(22, 13 + l, 22 + l, 13)
      }
    }

    header = JLabel(title).derive(15).apply {
      foreground = ENV.lightForeground
      alignmentY = Component.TOP_ALIGNMENT
    }

    chain(box(false) {
      preferredSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      minimumSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      maximumSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      spring()
      add(header)
      spring()
      add(closeButton)
    })
  }

  private fun chain(
    comp: JComponent,
    xOffset: Int? = null,
    yOffset: Int? = null,
    squash: Boolean = false
  ): Component {
    comp.alignmentX = Component.LEFT_ALIGNMENT
    yOffset?.let { gap(it) }
    if (xOffset != null || squash) {
      add(box(false) {
        alignmentX = Component.LEFT_ALIGNMENT
        xOffset?.let { gap(it) }
        add(comp)
        if (squash) spring()
      })
    } else add(comp)
    return comp
  }

  fun chooser(location: String): DirectoryChooser = DirectoryChooser(
    textField(location),
    button("Select Folder"),
    listener
  )

  fun label(value: String, offset: Int = 40): JLabel =
    JLabel(value).derive(5).also {
      chain(it, yOffset = offset)
    }

  fun description(value: String, offset: Int = 8): JLabel =
    JLabel(value).derive(-5).also {
      chain(it, yOffset = offset)
    }

  fun labelButton(offset: Int = 0, block: LabelButton.() -> Unit): LabelButton =
    LabelButton(builder = block).also {
      chain(it, yOffset = offset)
    }

  fun checkBox(name: String, selected: Boolean, offset: Int = 8): JCheckBox =
    JCheckBox(name, selected).also {
      chain(it, yOffset = offset, xOffset = 15)
    }

  fun <T> comboBox(options: Array<T>, selected: T?, offset: Int = 8): JComboBox<T> =
    JComboBox(options).apply {
      preferredSize = Dimension(preferredSize.width, 40)
      maximumSize = Dimension(maximumSize.width, 40)
      selectedItem = selected ?: selectedItem
      chain(this, yOffset = offset, xOffset = 15)
    }

  fun textField(value: String, offset: Int = 4): JTextField =
    JTextField(value, TEXT_COLUMNS).apply {
      preferredSize = Dimension(preferredSize.width, 40)
      maximumSize = Dimension(maximumSize.width, 40)
      chain(this, yOffset = offset, xOffset = 15)
    }

  fun button(value: String, offset: Int = 4): JButton =
    JButton(value).also {
      chain(it, yOffset = offset, xOffset = 15)
    }

  fun slider(
    indicator: JLabel,
    min: Int,
    max: Int,
    value: Int,
    tick: Int = 1.coerceAtLeast((max - min) / 20),
    step: Int = 1.coerceAtLeast((max - min) / 4),
    offset: Int = 2
  ): Slider = Slider(min, max, value, tick, step, indicator.text, indicator).also {
    chain(it, yOffset = offset)
  }

  companion object {
    const val HARD_WIDTH = 800
    const val TEXT_COLUMNS = 38
  }
}