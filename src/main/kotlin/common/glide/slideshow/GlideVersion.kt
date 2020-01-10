package common.glide.slideshow

import common.glide.storage.Persistable
import kotlinx.serialization.Serializable

@Serializable
class GlideVersion(val value: Int? = null) : Persistable<GlideVersion>(serializer())
