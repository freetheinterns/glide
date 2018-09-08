package gui

import utils.extensions.sizeTo
import java.awt.Color
import java.awt.Component
import java.awt.event.ActionListener
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JTextField
import javax.swing.SpringLayout
import javax.swing.SpringLayout.NORTH
import javax.swing.SpringLayout.SOUTH
import javax.swing.SpringLayout.WEST
import javax.swing.SpringLayout.HORIZONTAL_CENTER

class TabPanel(
        title: String,
        totalHeight: Int,
        listener: ActionListener
) : JPanel() {
  val label = TabLabel(title, listener)
  var header = JLabel(title)
  var spring = SpringLayout()
  var memory: Component

  init {
    // Set overall dimensions
    sizeTo(HARD_WIDTH, totalHeight)
    layout = spring

    // Build Header
    header.font = header.font.deriveFont(header.font.size2D + 15)
    header.foreground = fgc
    background = bgc
    memory = header
    super.add(header)
    spring.putConstraint(HORIZONTAL_CENTER, header, PADDING, HORIZONTAL_CENTER, this)
  }

  companion object {
    const val HARD_WIDTH = 800
    private const val textColumns = 38
    private const val PADDING = 20

    val bgc = Color.BLACK
    val fgc = Color.WHITE
  }

  private class Label(value: String) : JLabel(value) {
    init {
      background = bgc
      foreground = fgc
    }
  }

  private class Description(value: String) : JLabel(value) {
    init {
      background = bgc
      foreground = fgc
    }
  }

  private class CheckBox(name: String, selected: Boolean) : JCheckBox(name, selected) {
    init {
      background = bgc
      foreground = fgc
    }
  }

  private class TextField(value: String) : JTextField(value, textColumns) {
    init {
      background = bgc
      foreground = fgc
    }
  }

  private class ComboBox<T>(options: Array<T>?) : JComboBox<T>(options) {
    init {
      background = bgc
      foreground = fgc
    }
  }

  override fun add(item: Component): Component {
    val verticalPad = when (item) {
      is Label       -> PADDING * 2
      is Description -> PADDING / 2
      else           -> 0
    }

    val ret = super.add(item)
    spring.putConstraint(NORTH, item, verticalPad, SOUTH, memory)
    spring.putConstraint(WEST, item, PADDING, WEST, this)
    memory = item
    return ret
  }

  fun buildCheckBox(name: String, selected: Boolean): JCheckBox {
    val box = CheckBox(name, selected)
    box.alignmentX = Component.LEFT_ALIGNMENT

    add(box)

    return box
  }

  fun <T> buildComboBox(name: String, options: Array<T>?, selected: T?): JComboBox<T> {
    val box = ComboBox(options)
    box.selectedItem = selected ?: box.selectedItem
    box.alignmentX = Component.LEFT_ALIGNMENT

    add(Label(name))
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
    slider.background = bgc
    slider.foreground = fgc

    add(Label(name))
    add(indicator)
    add(slider)

    return slider
  }

  fun buildTextField(name: String, value: String = ""): JTextField {
    val field = TextField(value)
    field.alignmentX = Component.LEFT_ALIGNMENT

    add(Label(name))
    add(field)

    return field
  }
}