package common.glide.storage

import java.io.Serializable

data class FileMapSchema(
  val mapData: HashMap<Serializable, Serializable?>
) : Serializable
