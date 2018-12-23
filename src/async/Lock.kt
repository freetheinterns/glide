package async

class Lock(private val defaultKey: Any? = null, private val defaultThrottle: Long? = null) {
  fun throttle(key: Any? = defaultKey, millis: Long? = defaultThrottle) =
    GlobalLocks.throttle(key!!, millis ?: 0)

  fun block(key: Any? = defaultKey, cb: () -> Unit = {}) =
    GlobalLocks.block(key!!, cb)

  fun softTry(key: Any? = defaultKey, cb: () -> Unit = {}) =
    GlobalLocks.softTry(key!!, cb)
}