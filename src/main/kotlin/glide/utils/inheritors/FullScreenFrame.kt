package glide.utils.inheritors

import java.awt.GraphicsEnvironment
import javax.swing.JFrame

open class FullScreenFrame :
  JFrame(
    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
  )
