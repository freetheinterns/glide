package common.glide.storage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


val JSON by lazy { Json(JsonConfiguration.Stable) }
val ENV by lazy { SlideshowSettings().load() }
val KEY_BINDINGS by lazy { KeyBindings().load() }
val FILE_SIZES by lazy { FileSizeMemoizer().load() }
