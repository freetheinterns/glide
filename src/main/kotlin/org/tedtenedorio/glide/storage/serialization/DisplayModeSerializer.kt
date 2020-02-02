package org.tedtenedorio.glide.storage.serialization

import java.awt.DisplayMode

object DisplayModeSerializer : ClassPropertySerializer<DisplayMode>() {
  override val className: String = "DisplayMode"
  override val classConstructor = ::DisplayMode
  override val properties = listOf(
    DisplayMode::getWidth,
    DisplayMode::getHeight,
    DisplayMode::getBitDepth,
    DisplayMode::getRefreshRate
  )
}