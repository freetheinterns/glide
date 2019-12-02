package glide.async

class Lock(val key: Any, private val defaultThrottle: Long? = null) {
  fun throttle(millis: Long? = defaultThrottle) =
    GlobalLocks.throttle(key, millis ?: 0)

  fun runLocked(callback: () -> Unit) =
    GlobalLocks.runLocked(key, callback)

  val isLocked: Boolean
    get() = GlobalLocks.isLocked(key)

  val lockedThreadName: String
    get() = "Locked Execution of $key"
}