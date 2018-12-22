import gui.Launcher
import storage.ENV
import utils.extensions.invert
import java.awt.Color
import java.awt.GraphicsEnvironment
import javax.swing.UIManager
import javax.swing.plaf.ColorUIResource


fun main(args: Array<String>) {
  // Use the system look and feel to appear more native
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

  // FONT_FAMILIES.forEach { font -> println(font) }
  // println(UIManager.getDefaults())

  // Increase all font sizes by 5
  UIManager.getDefaults().toString(). // "{k.font=v, k=v, k.font=v, k=v, k.font=v}"
    removePrefix("{").                // "k.font=v, k=v, k.font=v, k=v, k.font=v}"
    split(", ").                      // ["k.font=v", "k=v", "k.font=v", "k=v", "k.font=v}"]
    map { it.split("=").first() }.    // ["k.font", "k", "k.font", "k", "k.font"]
    filter { it.contains(".font") }.  // ["k.font", "k.font", "k.font"]
    forEach { UIManager.getFont(it)?.let { font -> UIManager.put(it, font.deriveFont(font.size2D + 5L)) } }

  fun putColor(key: Any, color: Color) = UIManager.put(key, ColorUIResource(color))
  fun putBgAndFg(key: String, bg: Color = ENV.background, fg: Color = ENV.foreground) {
    putColor("$key${if (key.endsWith(".")) "f" else "F"}oreground", fg)
    putColor("$key${if (key.endsWith(".")) "b" else "B"}ackground", bg)
  }

  fun invertColor(key: Any) = UIManager.put(key, UIManager.getColor(key).invert)
  fun invertBgAndFg(key: String) {
    invertColor("$key${if (key.endsWith(".")) "f" else "F"}oreground")
    invertColor("$key${if (key.endsWith(".")) "b" else "B"}ackground")
  }

  // invertColor("Button.background")
  // invertColor("Button.darkShadow")
  // invertColor("Button.disabledForeground")
  // invertColor("Button.disabledShadow")
  // invertColor("Button.foreground")
  // invertColor("Button.highlight")
  // invertColor("Button.light")
  // invertColor("Button.shadow")

  invertColor("CheckBox.background")
  invertColor("CheckBox.darkShadow")
  invertColor("CheckBox.foreground")
  invertColor("CheckBox.highlight")
  invertColor("CheckBox.interiorBackground")
  invertColor("CheckBox.light")
  invertColor("CheckBox.shadow")
  invertColor("CheckBoxMenuItem.acceleratorForeground")
  invertColor("CheckBoxMenuItem.acceleratorSelectionForeground")
  invertColor("CheckBoxMenuItem.background")
  invertColor("CheckBoxMenuItem.foreground")
  invertColor("CheckBoxMenuItem.selectionBackground")
  invertColor("CheckBoxMenuItem.selectionForeground")
  //  invertColor("ColorChooser.background")
  //  invertColor("ColorChooser.foreground")
  //  invertColor("ColorChooser.swatchesDefaultRecentColor")
  //  invertColor("ComboBox.background")
  //  invertColor("ComboBox.buttonBackground")
  //  invertColor("ComboBox.buttonDarkShadow")
  //  invertColor("ComboBox.buttonHighlight")
  //  invertColor("ComboBox.buttonShadow")
  //  invertColor("ComboBox.disabledBackground")
  //  invertColor("ComboBox.disabledForeground")
  //  invertColor("ComboBox.foreground")
  //  invertColor("ComboBox.selectionBackground")
  //  invertColor("ComboBox.selectionForeground")
  //  invertColor("Desktop.background")
  //  invertColor("EditorPane.background")
  //  invertColor("EditorPane.caretForeground")
  //  invertColor("EditorPane.disabledBackground")
  //  invertColor("EditorPane.foreground")
  //  invertColor("EditorPane.inactiveBackground")
  //  invertColor("EditorPane.inactiveForeground")
  //  invertColor("EditorPane.selectionBackground")
  //  invertColor("EditorPane.selectionForeground")
  //  invertColor("FileChooser.listViewBackground")
  //  invertColor("FormattedTextField.background")
  //  invertColor("FormattedTextField.caretForeground")
  //  invertColor("FormattedTextField.disabledBackground")
  //  invertColor("FormattedTextField.foreground")
  //  invertColor("FormattedTextField.inactiveBackground")
  //  invertColor("FormattedTextField.inactiveForeground")
  //  invertColor("FormattedTextField.selectionBackground")
  //  invertColor("FormattedTextField.selectionForeground")
  //  invertColor("InternalFrame.activeBorderColor")
  //  invertColor("InternalFrame.activeTitleBackground")
  //  invertColor("InternalFrame.activeTitleForeground")
  //  invertColor("InternalFrame.activeTitleGradient")
  //  invertColor("InternalFrame.borderColor")
  //  invertColor("InternalFrame.borderDarkShadow")
  //  invertColor("InternalFrame.borderHighlight")
  //  invertColor("InternalFrame.borderLight")
  //  invertColor("InternalFrame.borderShadow")
  //  invertColor("InternalFrame.inactiveBorderColor")
  //  invertColor("InternalFrame.inactiveTitleBackground")
  //  invertColor("InternalFrame.inactiveTitleForeground")
  //  invertColor("InternalFrame.inactiveTitleGradient")
  //  invertColor("InternalFrame.minimizeIconBackground")
  //  invertColor("InternalFrame.resizeIconHighlight")
  //  invertColor("InternalFrame.resizeIconShadow")
  invertColor("Label.background")
  invertColor("Label.disabledForeground")
  invertColor("Label.disabledShadow")
  invertColor("Label.foreground")
  //  invertColor("List.background")
  //  invertColor("List.dropLineColor")
  //  invertColor("List.foreground")
  //  invertColor("List.selectionBackground")
  //  invertColor("List.selectionForeground")
  //  invertColor("Menu.acceleratorForeground")
  //  invertColor("Menu.acceleratorSelectionForeground")
  //  invertColor("Menu.background")
  //  invertColor("Menu.foreground")
  //  invertColor("Menu.selectionBackground")
  //  invertColor("Menu.selectionForeground")
  //  invertColor("MenuBar.background")
  //  invertColor("MenuBar.foreground")
  //  invertColor("MenuBar.highlight")
  //  invertColor("MenuBar.shadow")
  //  invertColor("MenuItem.acceleratorForeground")
  //  invertColor("MenuItem.acceleratorSelectionForeground")
  //  invertColor("MenuItem.background")
  //  invertColor("MenuItem.disabledForeground")
  //  invertColor("MenuItem.foreground")
  //  invertColor("MenuItem.selectionBackground")
  //  invertColor("MenuItem.selectionForeground")
  //  invertColor("OptionPane.background")
  //  invertColor("OptionPane.foreground")
  //  invertColor("OptionPane.messageForeground")
  //  invertColor("Panel.background")
  //  invertColor("Panel.foreground")
  //  invertColor("PasswordField.background")
  //  invertColor("PasswordField.caretForeground")
  //  invertColor("PasswordField.disabledBackground")
  //  invertColor("PasswordField.foreground")
  //  invertColor("PasswordField.inactiveBackground")
  //  invertColor("PasswordField.inactiveForeground")
  //  invertColor("PasswordField.selectionBackground")
  //  invertColor("PasswordField.selectionForeground")
  //  invertColor("PopupMenu.background")
  //  invertColor("PopupMenu.foreground")
  //  invertColor("ProgressBar.background")
  //  invertColor("ProgressBar.foreground")
  //  invertColor("ProgressBar.highlight")
  //  invertColor("ProgressBar.selectionBackground")
  //  invertColor("ProgressBar.selectionForeground")
  //  invertColor("ProgressBar.shadow")
  //  invertColor("RadioButton.background")
  //  invertColor("RadioButton.darkShadow")
  //  invertColor("RadioButton.foreground")
  //  invertColor("RadioButton.highlight")
  //  invertColor("RadioButton.interiorBackground")
  //  invertColor("RadioButton.light")
  //  invertColor("RadioButton.shadow")
  //  invertColor("RadioButtonMenuItem.acceleratorForeground")
  //  invertColor("RadioButtonMenuItem.acceleratorSelectionForeground")
  //  invertColor("RadioButtonMenuItem.background")
  //  invertColor("RadioButtonMenuItem.disabledForeground")
  //  invertColor("RadioButtonMenuItem.foreground")
  //  invertColor("RadioButtonMenuItem.selectionBackground")
  //  invertColor("RadioButtonMenuItem.selectionForeground")
  //  invertColor("ScrollBar.background")
  //  invertColor("ScrollBar.foreground")
  //  invertColor("ScrollBar.thumb")
  //  invertColor("ScrollBar.thumbDarkShadow")
  //  invertColor("ScrollBar.thumbHighlight")
  //  invertColor("ScrollBar.thumbShadow")
  //  invertColor("ScrollBar.track")
  //  invertColor("ScrollBar.trackForeground")
  //  invertColor("ScrollBar.trackHighlight")
  //  invertColor("ScrollBar.trackHighlightForeground")
  //  invertColor("ScrollPane.background")
  //  invertColor("ScrollPane.foreground")
  //  invertColor("Separator.background")
  //  invertColor("Separator.foreground")
  //  invertColor("Separator.highlight")
  //  invertColor("Separator.shadow")
  //  invertColor("Slider.background")
  //  invertColor("Slider.focus")
  //  invertColor("Slider.foreground")
  //  invertColor("Slider.highlight")
  //  invertColor("Slider.shadow")
  //  invertColor("Spinner.background")
  //  invertColor("Spinner.foreground")
  //  invertColor("SplitPane.background")
  //  invertColor("SplitPane.darkShadow")
  //  invertColor("SplitPane.highlight")
  //  invertColor("SplitPane.shadow")
  //  invertColor("SplitPaneDivider.draggingColor")
  //  invertColor("TabbedPane.background")
  //  invertColor("TabbedPane.darkShadow")
  //  invertColor("TabbedPane.focus")
  //  invertColor("TabbedPane.foreground")
  //  invertColor("TabbedPane.highlight")
  //  invertColor("TabbedPane.light")
  //  invertColor("TabbedPane.shadow")
  //  invertColor("Table.background")
  //  invertColor("Table.darkShadow")
  //  invertColor("Table.dropLineColor")
  //  invertColor("Table.dropLineShortColor")
  //  invertColor("Table.focusCellBackground")
  //  invertColor("Table.focusCellForeground")
  //  invertColor("Table.foreground")
  //  invertColor("Table.gridColor")
  //  invertColor("Table.highlight")
  //  invertColor("Table.light")
  //  invertColor("Table.selectionBackground")
  //  invertColor("Table.selectionForeground")
  //  invertColor("Table.shadow")
  //  invertColor("Table.sortIconColor")
  //  invertColor("Table.sortIconHighlight")
  //  invertColor("Table.sortIconLight")
  //  invertColor("TableHeader.background")
  //  invertColor("TableHeader.foreground")
  //  invertColor("TextArea.background")
  //  invertColor("TextArea.caretForeground")
  //  invertColor("TextArea.disabledBackground")
  //  invertColor("TextArea.foreground")
  //  invertColor("TextArea.inactiveBackground")
  //  invertColor("TextArea.inactiveForeground")
  //  invertColor("TextArea.selectionBackground")
  //  invertColor("TextArea.selectionForeground")
  //  invertColor("TextField.background")
  //  invertColor("TextField.caretForeground")
  //  invertColor("TextField.darkShadow")
  //  invertColor("TextField.disabledBackground")
  //  invertColor("TextField.foreground")
  //  invertColor("TextField.highlight")
  //  invertColor("TextField.inactiveBackground")
  //  invertColor("TextField.inactiveForeground")
  //  invertColor("TextField.light")
  //  invertColor("TextField.selectionBackground")
  //  invertColor("TextField.selectionForeground")
  //  invertColor("TextField.shadow")
  //  invertColor("TextPane.background")
  //  invertColor("TextPane.caretForeground")
  //  invertColor("TextPane.disabledBackground")
  //  invertColor("TextPane.foreground")
  //  invertColor("TextPane.inactiveBackground")
  //  invertColor("TextPane.inactiveForeground")
  //  invertColor("TextPane.selectionBackground")
  //  invertColor("TextPane.selectionForeground")
  //  invertColor("TitledBorder.titleColor")
  //  invertColor("ToggleButton.background")
  //  invertColor("ToggleButton.darkShadow")
  //  invertColor("ToggleButton.focus")
  //  invertColor("ToggleButton.foreground")
  //  invertColor("ToggleButton.highlight")
  //  invertColor("ToggleButton.light")
  //  invertColor("ToggleButton.shadow")
  //  invertColor("ToolBar.background")
  //  invertColor("ToolBar.darkShadow")
  //  invertColor("ToolBar.dockingBackground")
  //  invertColor("ToolBar.dockingForeground")
  //  invertColor("ToolBar.floatingBackground")
  //  invertColor("ToolBar.floatingForeground")
  //  invertColor("ToolBar.foreground")
  //  invertColor("ToolBar.highlight")
  //  invertColor("ToolBar.light")
  //  invertColor("ToolBar.shadow")
  //  invertColor("ToolTip.background")
  //  invertColor("ToolTip.foreground")
  //  invertColor("Tree.background")
  //  invertColor("Tree.dropLineColor")
  //  invertColor("Tree.foreground")
  //  invertColor("Tree.hash")
  //  invertColor("Tree.selectionBackground")
  //  invertColor("Tree.selectionBorderColor")
  //  invertColor("Tree.selectionForeground")
  //  invertColor("Tree.textBackground")
  //  invertColor("Tree.textForeground")
  //  invertColor("Viewport.background")
  //  invertColor("Viewport.foreground")
  //  invertColor("activeCaption")
  //  invertColor("activeCaptionBorder")
  //  invertColor("activeCaptionText")
  //  invertColor("control")
  //  invertColor("controlDkShadow")
  //  invertColor("controlHighlight")
  //  invertColor("controlLtHighlight")
  //  invertColor("controlShadow")
  //  invertColor("controlText")
  //  invertColor("desktop")
  //  invertColor("inactiveCaption")
  //  invertColor("inactiveCaptionBorder")
  //  invertColor("inactiveCaptionText")
  //  invertColor("info")
  //  invertColor("infoText")
  //  invertColor("menu")
  //  invertColor("menuPressedItemB")
  //  invertColor("menuPressedItemF")
  //  invertColor("menuText")
  //  invertColor("scrollbar")
  //  invertColor("text")
  //  invertColor("textHighlight")
  //  invertColor("textHighlightText")
  //  invertColor("textInactiveText")
  //  invertColor("textText")
  //  invertColor("window")
  //  invertColor("windowBorder")
  //  invertColor("windowText")

  try {
    Launcher()
    //    Projector()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}