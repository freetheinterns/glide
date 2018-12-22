package storage

import slideshow.Projector
import storage.ENV.archive
import storage.ENV.debounce
import storage.ENV.direction
import storage.ENV.imageBufferCapacity
import storage.ENV.imagePattern
import storage.ENV.intraPlaylistVision
import storage.ENV.ordering
import storage.ENV.paneled
import storage.ENV.root
import storage.ENV.scaling
import storage.ENV.showFooterFileNumber
import storage.ENV.showMarginFileCount
import storage.ENV.showMarginFileName
import storage.ENV.showMarginFolderCount
import storage.ENV.showMarginFolderName
import storage.ENV.speed
import storage.ENV.verbose
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.Image
import java.io.File
import javax.swing.UIManager
import kotlin.properties.Delegates

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
object ENV : FileMap("environment") {
  init {
    load()
  }

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

  var projector: Projector by Delegates.notNull()

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