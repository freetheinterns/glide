package common.glide.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration.Companion.Stable


val JSON by lazy {
  Json(Stable.copy(strictMode = false, prettyPrint = true))
}

val ENV by lazy { SlideshowSettings().load() }
val KEY_BINDINGS by lazy { KeyBindings().load() }
val FILE_SIZES by lazy { FileSizeMemoizer().load() }
