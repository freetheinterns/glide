package common.glide.storage.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

val JSON by lazy {
  Json(JsonConfiguration.Stable.copy(strictMode = false, prettyPrint = true))
}
