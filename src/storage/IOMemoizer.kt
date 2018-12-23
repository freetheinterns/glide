package storage

object IOMemoizer : FileMapTTL("cache", 86400000L) {
  init {
    load()
  }
}