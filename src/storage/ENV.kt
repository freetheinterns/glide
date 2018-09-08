package storage

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
import storage.schemas.ENVSchema
import utils.inheritors.Persistable
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
object ENV : Persistable<ENVSchema>() {
  override val persistedLocation = File("environment.java.object")

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

  var archive: String = File("~\\Pictures\\archive").absolutePath
  var fontName: String = UIManager.getFont("Button.font").fontName
  var ordering: String = FILE_COUNT
  var root: String = "~\\Pictures"

  var direction: Boolean = true
  var paneled: Boolean = true
  var verbose: Boolean = true

  var showFooterFileNumber: Boolean = true
  var showMarginFileCount: Boolean = true
  var showMarginFileName: Boolean = true
  var showMarginFolderCount: Boolean = true
  var showMarginFolderName: Boolean = true

  var imageBufferCapacity: Int = 2
  var intraPlaylistVision: Int = 50
  var scaling: Int = Image.SCALE_AREA_AVERAGING
  var speed: Int = 2500

  var debounce: Long = 200L

  var imagePattern: Regex = "^.+\\.(jpg|png|gif|bmp)$".toRegex(RegexOption.IGNORE_CASE)

  override fun toSerializedInstance() = ENVSchema(
    archive,
    fontName,
    ordering,
    root,
    direction,
    paneled,
    verbose,
    showFooterFileNumber,
    showMarginFileCount,
    showMarginFileName,
    showMarginFolderCount,
    showMarginFolderName,
    imageBufferCapacity,
    intraPlaylistVision,
    scaling,
    speed,
    debounce,
    imagePattern
  )
}