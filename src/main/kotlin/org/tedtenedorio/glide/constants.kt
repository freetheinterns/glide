package org.tedtenedorio.glide

import org.openjdk.jmh.infra.Blackhole
import org.tedtenedorio.glide.extensions.threadPoolDispatcher
import org.tedtenedorio.glide.storage.Persist.load
import org.tedtenedorio.glide.storage.schemas.SlideshowSettings
import java.awt.GraphicsEnvironment

const val GB: Long = 1024 * 1024 * 1024

val ENV by lazy { SlideshowSettings().load() }

val BLACKHOLE = Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.")

val USER_HOME: String by lazy { System.getProperty("user.home") }
val FONT_FAMILIES: Array<String> by lazy {
  GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
}

val EVENT_DISPATCHER = threadPoolDispatcher(coreThreads = 4, maxThreads = 12, name = "event-handler")
val BACKGROUND_DISPATCHER = threadPoolDispatcher(coreThreads = 4, name = "background")
