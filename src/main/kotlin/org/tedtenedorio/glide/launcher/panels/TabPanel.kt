package org.tedtenedorio.glide.launcher.panels

import org.tedtenedorio.glide.ENV
import org.tedtenedorio.glide.extensions.deriveFont
import org.tedtenedorio.glide.extensions.gap
import org.tedtenedorio.glide.extensions.perpendicularBox
import org.tedtenedorio.glide.extensions.spring
import org.tedtenedorio.glide.launcher.Launcher
import org.tedtenedorio.glide.launcher.components.DirectoryChooser
import org.tedtenedorio.glide.launcher.components.LabelButton
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
import javax.swing.JSlider
import javax.swing.JTextField
import javax.swing.border.EmptyBorder
import kotlin.system.exitProcess

abstract class TabPanel(
  title: String,
  private val listener: Launcher
) : Box(BoxLayout.Y_AXIS) {
  var highlighted = false
  val label = LabelButton(title = title, listener = listener)

  private val closeButton: LabelButton
  private val header: JLabel

  init {
    isOpaque = true
    border = EmptyBorder(0, 7, 0, 0)

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

    header = JLabel(title).deriveFont(15).apply {
      foreground = ENV.lightForeground
      alignmentY = Component.TOP_ALIGNMENT
    }

    perpendicularBox {
      preferredSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      minimumSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      maximumSize = Dimension(HARD_WIDTH, LabelButton.HARD_HEIGHT)
      spring()
      add(header)
      spring()
      add(closeButton)
    }
  }

  private fun chain(
    comp: JComponent,
    top: Int = 0,
    left: Int = 0,
    bottom: Int = 0,
    right: Int = 0
  ): Component {
    gap(top)
    perpendicularBox {
      gap(left)
      add(comp)
      gap(right)
      spring()
      maximumSize = Dimension(HARD_WIDTH, comp.preferredSize.height)
    }
    gap(bottom)
    return comp
  }

  fun chooser(
    location: String,
    callback: () -> Unit = {}
  ): DirectoryChooser {
    val f = textField(location)
    val b = button("Select Folder")
    return DirectoryChooser(f, listener, b, callback)
  }

  fun label(value: String, offset: Int = 40): JLabel =
    JLabel(value).deriveFont(5).also {
      chain(it, top = offset, left = 20)
    }

  fun description(value: String, offset: Int = 8): JLabel =
    JLabel(value).deriveFont(-5).also {
      chain(it, top = offset, left = 20)
    }

  fun checkBox(name: String, selected: Boolean, offset: Int = 8): JCheckBox =
    JCheckBox(name, selected).also {
      it.addActionListener { listener.save() }
      chain(it, top = offset, left = 35)
    }

  fun <T> comboBox(options: Array<T>, selected: T?, offset: Int = 8): JComboBox<T> =
    JComboBox(options).apply {
      preferredSize = Dimension(preferredSize.width, 40)
      maximumSize = Dimension(maximumSize.width, 40)
      selectedItem = selected ?: selectedItem
      addActionListener { listener.save() }
      chain(this, top = offset, left = 35)
    }

  fun textField(value: String, offset: Int = 4): JTextField =
    JTextField(value, TEXT_COLUMNS).apply {
      chain(this, top = offset, left = 35, right = 50)
    }

  fun button(value: String, offset: Int = 4): JButton =
    JButton(value).also {
      chain(it, top = offset, left = 35)
    }

  fun slider(
    indicator: JLabel,
    min: Int,
    max: Int,
    value: Int,
    tick: Int = 1.coerceAtLeast((max - min) / 20),
    step: Int = 1.coerceAtLeast((max - min) / 4),
    offset: Int = 2
  ): JSlider = JSlider(min, max, value).apply {
    val labelFormat = indicator.text
    background = ENV.background
    foreground = ENV.foreground
    minorTickSpacing = tick
    majorTickSpacing = step
    paintTicks = true
    paintLabels = true
    paintTrack = true
    preferredSize = Dimension(300, 50)
    font = font.deriveFont(font.size2D - 6L)
    alignmentX = Component.LEFT_ALIGNMENT
    addChangeListener {
      indicator.text = labelFormat.format(value)
      listener.save()
    }
    indicator.text = labelFormat.format(value)
    chain(this, top = offset)
  }

  companion object {
    const val HARD_WIDTH = 800
    const val TEXT_COLUMNS = 38
  }
}