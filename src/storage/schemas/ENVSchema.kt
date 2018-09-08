package storage.schemas

import java.io.Serializable

data class ENVSchema(
        val archive: String,
        val fontName: String,
        val ordering: String,
        val root: String,

        val direction: Boolean,
        val paneled: Boolean,
        val verbose: Boolean,

        val showFooterFileNumber: Boolean,
        val showMarginFileCount: Boolean,
        val showMarginFileName: Boolean,
        val showMarginFolderCount: Boolean,
        val showMarginFolderName: Boolean,

        val imageBufferCapacity: Int,
        val intraPlaylistVision: Int,
        val scaling: Int,
        val speed: Int,

        val debounce: Long,

        val imagePattern: Regex
) : Serializable