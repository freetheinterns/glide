package storage

object IOMemoizer : FileMap("cache") { init {
  load()
}
}