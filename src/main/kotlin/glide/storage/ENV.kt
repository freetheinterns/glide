package glide.storage

import glide.gui.Launcher
import glide.slideshow.Projector
import glide.storage.ENV.archive
import glide.storage.ENV.debounce
import glide.storage.ENV.direction
import glide.storage.ENV.imageBufferCapacity
import glide.storage.ENV.imagePattern
import glide.storage.ENV.intraPlaylistVision
import glide.storage.ENV.ordering
import glide.storage.ENV.paneled
import glide.storage.ENV.root
import glide.storage.ENV.scaling
import glide.storage.ENV.showFooterFileNumber
import glide.storage.ENV.showMarginFileCount
import glide.storage.ENV.showMarginFileName
import glide.storage.ENV.showMarginFolderCount
import glide.storage.ENV.showMarginFolderName
import glide.storage.ENV.speed
import glide.storage.ENV.verbose
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.io.File
import javax.swing.UIManager

/**
 * A global object (static) that is used to persist & access settings for this program
 *
 * @property archive
 * @property ordering
 * @property root
 *
 * @property direction
 * @property paneled
 * @property verbose
 *
 * @property showFooterFileNumber
 * @property showMarginFileCount
 * @property showMarginFileName
 * @property showMarginFolderCount
 * @property showMarginFolderName
 *
 * @property imageBufferCapacity
 * @property intraPlaylistVision
 * @property scaling
 * @property speed
 *
 * @property debounce
 *
 * @property imagePattern
 */
object ENV : FileMap() {
  const val ALPHABETICAL = "Alphabetical"
  const val FILE_COUNT = "# of Files"
  const val FOLDER_ACCESSED = "Folder Accessed@"
  const val FOLDER_CREATED = "Folder Created@"
  const val FOLDER_DATA = "Data MB"
  const val FOLDER_UPDATED = "Folder Updated@"
  val ORDER_ENUMS = arrayOf(
    ALPHABETICAL,
    FILE_COUNT,
    FOLDER_ACCESSED,
    FOLDER_CREATED,
    FOLDER_DATA,
    FOLDER_UPDATED
  )
  val FONT_FAMILIES: Array<String> = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames

  var scope = ""
  var projector: Projector? = null
  var launcher: Launcher? = null

  var background by fileData(Color(15, 15, 15))
  var foreground by fileData(Color(0, 0, 0))
  var dark by fileData(Color(27, 28, 27))
  var darkSelected by fileData(Color(70, 71, 71))
  var darkHighlight by fileData(Color(103, 102, 100))
  var exitRed by fileData(Color(232, 17, 35))

  var archive: String by fileData(File("~\\Pictures\\archive").absolutePath)
  var fontName: String by fileData(UIManager.getFont("Button.font").fontName)
  var ordering by fileData(FILE_COUNT)
  var root by fileData("~\\Pictures")

  var direction by fileData(true)
  var paneled by fileData(true)
  var verbose by fileData(true)

  var showFooterFileNumber by fileData(true)
  var showMarginFileCount by fileData(true)
  var showMarginFileName by fileData(true)
  var showMarginFolderCount by fileData(true)
  var showMarginFolderName by fileData(true)

  var imageBufferCapacity by fileData(2)
  var intraPlaylistVision by fileData(50)
  var scaling by fileData(Image.SCALE_AREA_AVERAGING)
  var speed by fileData(2500)

  var debounce by fileData(200L)

  var imagePattern by fileData("^.+\\.(jpg|png|gif|bmp)$".toRegex(RegexOption.IGNORE_CASE))
}