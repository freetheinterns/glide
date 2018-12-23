package utils.extensions

import java.awt.Color
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.DisplayMode
import java.awt.Font
import java.awt.GraphicsDevice
import java.awt.GridBagConstraints
import java.awt.Image
import java.awt.Insets
import java.awt.event.KeyListener
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SpringLayout


///////////////////////////////////////
// GraphicsDevice Extensions
///////////////////////////////////////

val GraphicsDevice.bestDisplayMode: DisplayMode?
  get() {
    for (x in BEST_DISPLAY_MODES.indices) {
      for (i in displayModes.indices) {
        if (displayModes[i].width == BEST_DISPLAY_MODES[x].width
            && displayModes[i].height == BEST_DISPLAY_MODES[x].height
            && displayModes[i].bitDepth == BEST_DISPLAY_MODES[x].bitDepth
        ) {
          return BEST_DISPLAY_MODES[x]
        }
      }
    }
    return null
  }

fun GraphicsDevice.chooseBestDisplayMode() {
  val nextMode = bestDisplayMode
  if (nextMode != null) {
    displayMode = nextMode
  }
}


///////////////////////////////////////
// Layout Extensions
///////////////////////////////////////

fun SpringLayout.glue(face: String, root: Component, anchor: Component, padding: Int = 0) =
  putConstraint(face, root, padding, face, anchor)

///////////////////////////////////////
// Color Extensions
///////////////////////////////////////

val Color.invert: Color
  get() = Color(255 - red, 255 - green, 255 - blue)

///////////////////////////////////////
// Image Extensions
///////////////////////////////////////

val Image.width: Int
  get() = getWidth(null)

val Image.height: Int
  get() = getHeight(null)


///////////////////////////////////////
// Font Extensions
///////////////////////////////////////

val Font.string: String
  get() = "<Font: $fontName $size $style>"

fun JComponent.sizeTo(w: Int, h: Int) {
  minimumSize = Dimension(w, h)
  preferredSize = Dimension(w, h)
  size = Dimension(w, h)
}

val Container.allComponents: List<Component>
  get() = this.components.flatMap {
    if (it is Container) {
      arrayListOf(it, *it.allComponents.toTypedArray()).asIterable()
    } else {
      arrayOf(it).asIterable()
    }
  }

fun Container.setShortcutListener(kl: KeyListener) {
  allComponents.forEach { it.addKeyListener(kl) }
}

fun JPanel.addGridBag(
        comp: JComponent,
        anchor: Int = GridBagConstraints.EAST,
        x: Int,
        y: Int,
        xSpan: Int = 1,
        ySpan: Int = 1,
        xPad: Int = 0,
        yPad: Int = 0,
        fill: Int = GridBagConstraints.EAST,
        insets: Insets = Insets(3, 3, 3, 3)
) {
  val c = GridBagConstraints()
  c.fill = fill
  c.weightx = 0.5
  c.gridx = x + 1
  c.gridy = y
  c.gridwidth = xSpan
  c.gridheight = ySpan
  c.anchor = anchor
  c.ipadx = xPad
  c.ipady = yPad
  c.insets = insets
  add(comp, c)
}