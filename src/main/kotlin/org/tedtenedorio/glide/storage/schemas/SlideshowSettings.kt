@file:UseSerializers(
  ColorSerializer::class,
  RegexSerializer::class,
  DisplayModeSerializer::class
)

package org.tedtenedorio.glide.storage.schemas

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.tedtenedorio.glide.GB
import org.tedtenedorio.glide.USER_HOME
import org.tedtenedorio.glide.enums.FolderSortStrategy
import org.tedtenedorio.glide.storage.Versionable
import org.tedtenedorio.glide.storage.serialization.ColorSerializer
import org.tedtenedorio.glide.storage.serialization.DisplayModeSerializer
import org.tedtenedorio.glide.storage.serialization.RegexSerializer
import java.awt.Color
import java.awt.DisplayMode
import java.io.File
import javax.swing.UIManager

@Serializable
data class SlideshowSettings(
  override val version: Int = 0,
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
  var cacheSizeBytes: Long = GB * 3,
  var displayMode: DisplayMode = DisplayMode(2560, 1440, 32, 0),

  var ordering: FolderSortStrategy = FolderSortStrategy.Random,
  var root: String = File("$USER_HOME\\Pictures").absolutePath,
  var archive: String = File("$USER_HOME\\Pictures\\archive").absolutePath,
  var fontName: String = UIManager.getFont("Button.font").fontName
) : Versionable
