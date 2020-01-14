package common.glide.storage

import kotlinx.serialization.Serializable

@Serializable
class GlideVersion(val value: Int? = null) : Persistable<GlideVersion>(serializer())
