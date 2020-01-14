@file:UseSerializers(
  ColorSerializer::class,
  RegexSerializer::class
)

package common.glide.storage

import common.glide.USER_HOME
import common.glide.enums.FolderSortStrategy
import common.glide.gui.Launcher
import common.glide.slideshow.Projector
import common.glide.storage.serialization.ColorSerializer
import common.glide.storage.serialization.RegexSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import java.awt.Color
import java.io.File
import javax.swing.UIManager

@Serializable
data class SlideshowSettings(
  var background: Color = Color(15, 15, 15),
  var foreground: Color = Color(0, 0, 0),
  var lightForeground: Color = Color(200, 200, 200),
  var dark: Color = Color(27, 28, 27),
  var darkSelected: Color = Color(70, 71, 71),
  var darkHighlight: Color = Color(103, 102, 100),
  var exitRed: Color = Color(232, 17, 35),

  var imagePattern: Regex = "^.+\\.(jpe?g|png|gif|bmp)$".toRegex(RegexOption.IGNORE_CASE),

  var direction: Boolean = true,
  var paneled: Boolean = true,
  var verbose: Boolean = true,
  var showFooterFileNumber: Boolean = true,
  var showMarginFileCount: Boolean = true,
  var showMarginFileName: Boolean = true,
  var showMarginFolderCount: Boolean = true,
  var showMarginFolderName: Boolean = true,

  var maxImagesPerFrame: Int = 3,
  var speed: Int = 2500,
  var debounce: Long = 200L,

  var ordering: FolderSortStrategy = FolderSortStrategy.NumberOfFiles,
  var root: String = File("$USER_HOME\\Pictures").absolutePath,
  var archive: String = File("$USER_HOME\\Pictures\\archive").absolutePath,
  var fontName: String = UIManager.getFont("Button.font").fontName,

  @Transient var scope: String = "",
  @Transient var projector: Projector? = null,
  @Transient var launcher: Launcher? = null
) : Persistable<SlideshowSettings>(serializer())
