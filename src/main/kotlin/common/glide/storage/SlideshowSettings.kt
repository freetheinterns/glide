@file:UseSerializers(
  ColorSerializer::class,
  RegexSerializer::class
)

package common.glide.storage

import common.glide.gui.Launcher
import common.glide.slideshow.Projector
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.io.File
import javax.swing.UIManager

@Serializable
data class SlideshowSettings(
  var background: Color = Color(15, 15, 15),
  var foreground: Color = Color(0, 0, 0),
  var dark: Color = Color(27, 28, 27),
  var darkSelected: Color = Color(70, 71, 71),
  var darkHighlight: Color = Color(103, 102, 100),
  var exitRed: Color = Color(232, 17, 35),

  var imagePattern: Regex = "^.+\\.(jpg|png|gif|bmp)$".toRegex(RegexOption.IGNORE_CASE),

  var direction: Boolean = true,
  var paneled: Boolean = true,
  var verbose: Boolean = true,
  var showFooterFileNumber: Boolean = true,
  var showMarginFileCount: Boolean = true,
  var showMarginFileName: Boolean = true,
  var showMarginFolderCount: Boolean = true,
  var showMarginFolderName: Boolean = true,

  var maxImagesPerFrame: Int = 3,
  var scaling: Int = Image.SCALE_AREA_AVERAGING,
  var speed: Int = 2500,
  var debounce: Long = 200L,

  var ordering: String = FILE_COUNT,
  var root: String = "~\\Pictures",
  var archive: String = File("~\\Pictures\\archive").absolutePath,
  var fontName: String = UIManager.getFont("Button.font").fontName

) : Persistable<SlideshowSettings>(serializer()) {
  companion object {
    const val ALPHABETICAL = "Alphabetical"
    const val FILE_COUNT = "# of Files"
    const val FOLDER_ACCESSED = "Folder Accessed@"
    const val FOLDER_CREATED = "Folder Created@"
    const val FOLDER_DATA = "Data MB"
    const val FOLDER_UPDATED = "Folder Updated@"
    const val RANDOM_ORDER = "Random"
    val ORDER_ENUMS = arrayOf(
      ALPHABETICAL,
      FILE_COUNT,
      FOLDER_ACCESSED,
      FOLDER_CREATED,
      FOLDER_DATA,
      FOLDER_UPDATED,
      RANDOM_ORDER
    )
    val FONT_FAMILIES: Array<String> =
      GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
  }

  @Transient
  var scope = ""
  @Transient
  var projector: Projector? = null
  @Transient
  var launcher: Launcher? = null

}