package storage.schemas

import storage.TimestampedEntry
import java.io.Serializable

data class FileMapSchema(val mapData: HashMap<Serializable, TimestampedEntry>) : Serializable