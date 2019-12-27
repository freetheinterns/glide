package org.fte.glide.storage

import java.io.Serializable

data class FileMapSchema(
  val mapData: HashMap<Serializable, Serializable?>
) : Serializable
