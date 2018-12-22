package storage

object IOMemoizer : FileMap("cache", 86400000L) {
  init {
    load()
  }
}