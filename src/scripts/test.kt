import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.KeyEvent
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane


class TabbedPaneDemo : JPanel() {
  init {
    layout = GridLayout(1, 1)

    val tabbedPane = JTabbedPane()
    val icon = createImageIcon("images/middle.gif")
    val panel1 = makeTextPanel("Panel #1")
    tabbedPane.addTab("Tab 1", icon, panel1, "Does nothing")
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1)
    val panel2 = makeTextPanel("Panel #2")
    tabbedPane.addTab("Tab 2", icon, panel2, "Does twice as much nothing")
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2)
    val panel3 = makeTextPanel("Panel #3")
    tabbedPane.addTab("Tab 3", icon, panel3, "Still does nothing")
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3)
    val panel4 = makeTextPanel("Panel #4 (has a preferred size of 410 x 50).")
    panel4.preferredSize = Dimension(410, 50)
    tabbedPane.addTab("Tab 4", icon, panel4, "Does nothing at all")
    tabbedPane.setMnemonicAt(3, KeyEvent.VK_4)
    add(tabbedPane)
    //The following line enables to use scrolling tabs.
    tabbedPane.tabLayoutPolicy = JTabbedPane.SCROLL_TAB_LAYOUT
  }

  fun makeTextPanel(text: String): JComponent {
    val panel = JPanel(false)
    val filler = JLabel(text)
    filler.horizontalAlignment = JLabel.CENTER
    panel.layout = GridLayout(1, 1)
    panel.add(filler)
    return panel
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  fun createImageIcon(path: String): ImageIcon? {
    val imgURL = TabbedPaneDemo::class.java.getResource(path)
    return if (imgURL != null) {
      ImageIcon(imgURL)
    } else {
      null
    }
  }

  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from
   * the event dispatch thread.
   */
  companion object {
    fun createAndShowGUI() {
      //Create and set up the window.
      val frame = JFrame("TabbedPaneDemo")
      frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

      //Add content to the window.
      frame.add(TabbedPaneDemo(), BorderLayout.CENTER)

      //Display the window.
      frame.pack()
      frame.isVisible = true
    }
  }
}