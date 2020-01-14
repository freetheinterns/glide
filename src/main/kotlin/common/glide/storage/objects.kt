package common.glide.storage

import common.glide.slideshow.GlideVersion
import org.openjdk.jmh.infra.Blackhole
import java.awt.GraphicsEnvironment

const val VERSION: Int = 1

val LAST_VERSION: Int? by lazy { GlideVersion().load().value }
val ENV by lazy { SlideshowSettings().load() }
val KEY_BINDINGS by lazy { KeyBindings().load() }
val FILE_SIZES by lazy { FileSizeMemoizer().load() }
val FONT_FAMILIES: Array<String> by lazy {
  GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
}

val BLACKHOLE = Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.")