import gui.Launcher
import storage.ENV
import storage.FileMap
import gui.TabPanel
import storage.ENV.FONT_FAMILIES
import java.awt.GraphicsEnvironment
import java.io.File
import javax.swing.UIManager
import javax.swing.plaf.ColorUIResource


val FILE_SIZE_MEMOIZER = FileMap(File("cache.java.object"))

fun main(args: Array<String>) {
  try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    listOf(
      "Button.font",
      "CheckBox.font",
      "CheckBoxMenuItem.font",
      "ColorChooser.font",
      "ComboBox.font",
      "EditorPane.font",
      "FormattedTextField.font",
      "Label.font",
      "List.font",
      "Menu.font",
      "MenuBar.font",
      "MenuItem.font",
      "OptionPane.font",
      "Panel.font",
      "PasswordField.font",
      "PopupMenu.font",
      "ProgressBar.font",
      "RadioButton.font",
      "RadioButtonMenuItem.font",
      "ScrollPane.font",
      "Slider.font",
      "Spinner.font",
      "TabPanel.font",
      "Table.font",
      "TableHeader.font",
      "TextArea.font",
      "TextField.font",
      "TextPane.font",
      "TitledBorder.font",
      "ToggleButton.font",
      "ToolBar.font",
      "ToolTip.font",
      "Tree.font",
      "Viewport.font"
    ).forEach {
      UIManager.getFont(it)?.let {
          font ->
        UIManager.put(it, font.deriveFont(font.size2D + 5L))
      }
    }
    UIManager.put("ComboBox.background", ColorUIResource(TabPanel.bgc))
    UIManager.put("ComboBox.foreground", ColorUIResource(TabPanel.fgc))
    UIManager.put("JTextField.background", ColorUIResource(TabPanel.bgc))
    UIManager.put("ComboBox.selectionForeground", ColorUIResource(TabPanel.fgc))
    //    UIManager.getDefaults().toString().split(", ").sorted().filter { it.contains("javax.swing.plaf.ColorUIResource") }.forEach { println(it) }
    //    UIManager.put("Label.foreground", Color.WHITE)
    //    UIManager.put("windowBorder", Color(103, 102, 100))
    //    UIManager.put("panel.background", Color.black.brighter())
  } catch (e: Exception) {
    e.printStackTrace()
  }

  try {
    FILE_SIZE_MEMOIZER.load()
    ENV.load()
    FILE_SIZE_MEMOIZER.save()
    ENV.save()
    FONT_FAMILIES.forEach { font -> println(font) }
    Launcher()
    //    slideshow.Projector()
  } catch (e: Throwable) {
    e.printStackTrace()
  } finally {
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = null
  }
}