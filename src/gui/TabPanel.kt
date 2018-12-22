package gui

import storage.ENV
import utils.extensions.glue
import utils.extensions.sizeTo
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Component
import java.awt.event.ActionListener
import javax.swing.Box
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JTextField
import javax.swing.SpringLayout
import javax.swing.SpringLayout.EAST
import javax.swing.SpringLayout.HORIZONTAL_CENTER
import javax.swing.SpringLayout.NORTH
import javax.swing.SpringLayout.SOUTH
import javax.swing.SpringLayout.WEST

open class TabPanel(
        title: String,
        totalHeight: Int,
        private val listener: Launcher
) : JPanel() {
  val label = LabelButton(title, listener)
  var header = JLabel(title)
  var spring = SpringLayout()
  var memory: Component = header
  private val closeButton = LabelButton(
    "",
    ActionListener { System.exit(0) },
    defaultBackground = ENV.background,
    defaultSelected = ENV.exitRed,
    width = 56,
    height = 38,
    artist = { g ->
      g.color = ENV.foreground
      g.stroke = BasicStroke(1F)
      val l = 11
      g.drawLine(22, 13, 22 + l, 13 + l)
      g.drawLine(22, 13 + l, 22 + l, 13)
    }
  )


  init {
    // Set overall dimensions
    sizeTo(HARD_WIDTH, totalHeight)
    layout = spring
    background = ENV.background

    // Build Header
    header.font = header.font.deriveFont(header.font.size2D + 15)
    header.foreground = ENV.foreground

    // Add components
    super.add(closeButton)
    super.add(header)

    // Constrain components
    spring.glue(HORIZONTAL_CENTER, header, this, PADDING)
    spring.glue(NORTH, closeButton, this)
    spring.glue(EAST, closeButton, this)
  }

  companion object {
    const val HARD_WIDTH = 800
    private const val TEXT_COLUMNS = 38
    const val PADDING = 32
  }

  class Label(value: String) : JLabel(value) {
    init {
      font = font.deriveFont(font.size2D + 5L)
    }
  }

  class Description(value: String) : JLabel(value) {
    init {
      font = font.deriveFont(font.size2D - 5L)
      foreground = Color.gray
    }
  }

  class CheckBox(name: String, selected: Boolean) : JCheckBox(name, selected)
  class TextField(value: String) : JTextField(value, TEXT_COLUMNS)
  class ComboBox<T>(options: Array<T>?) : JComboBox<T>(options)

  override fun add(item: Component): Component {
    val verticalPad = when (item) {
      is Label       -> (PADDING * 1.25).toInt()
      is Description -> PADDING / 4
      is ComboBox<*> -> PADDING / 4
      is JSlider     -> PADDING / 16
      is CheckBox    -> PADDING / 4
      else           -> PADDING / 8
    }

    val ret = super.add(item)
    spring.putConstraint(NORTH, item, verticalPad, SOUTH, memory)
    spring.glue(WEST, item, this, PADDING)
    memory = item
    return ret
  }

  override fun add(item: Component, verticalPad: Int): Component {
    val ret = super.add(item)
    spring.putConstraint(NORTH, item, verticalPad, SOUTH, memory)
    spring.glue(WEST, item, this, PADDING)
    memory = item
    return ret
  }

  fun pad(height: Int) {
    add(Box.createVerticalStrut(height), 0)
  }

  fun buildCheckBox(
          name: String,
          selected: Boolean,
          description: String? = null
  ): JCheckBox {
    val box = CheckBox(name, selected)
    box.alignmentX = Component.LEFT_ALIGNMENT

    description?.let {
      add(Description(it))
      add(box, 0)
    }
    if (description == null)
      add(box)

    return box
  }

  fun <T> buildComboBox(
          name: String? = null,
          options: Array<T>?,
          selected: T?,
          description: String? = null
  ): JComboBox<T> {
    val box = ComboBox(options)
    box.selectedItem = selected ?: box.selectedItem
    box.alignmentX = Component.LEFT_ALIGNMENT

    name?.let { add(Label(it)) }
    description?.let { add(Description(it)) }
    add(box)

    return box
  }

  fun buildSlider(
          name: String,
          min: Int,
          max: Int,
          value: Int,
          labelFormat: String = "(%d)",
          tick: Int = Math.max(1, (max - min) / 20),
          step: Int = Math.max(1, (max - min) / 4)
  ): JSlider {
    val indicator = Description(labelFormat.format(value))
    val slider = Slider(min, max, value, tick, step, labelFormat, indicator)
    slider.background = ENV.background
    slider.foreground = ENV.foreground

    add(Label(name))
    add(indicator)
    add(slider)

    return slider
  }

  fun buildChooser(
          name: String,
          home: String,
          description: String? = null
  ): DirectoryChooser {
    val chooser = DirectoryChooser(home, listener)
    chooser.alignmentX = Component.LEFT_ALIGNMENT

    add(Label(name))
    description?.let { add(Description(it)) }
    add(chooser.banner)
    add(chooser.trigger)

    return chooser
  }
}