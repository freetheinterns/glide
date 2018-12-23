package async

import utils.extensions.always

object GlobalLocks {
  val now by always { System.currentTimeMillis() }

  private class Context {
    private val blocks = hashSetOf<Any>()
    val since by always { now - invokedAt }
    var invokedAt: Long = 0
      set(value) {
        field = if (value > field) value else field
      }

    fun invoke(cb: (() -> Unit)? = null): Boolean {
      invokedAt = now
      cb ?: return blocks.isNotEmpty()

      val crumb = Math.random()
      blocks.add(crumb)
      try {
        cb()
      } catch (err: Exception) {
        println(err.stackTrace.toString())
        err.printStackTrace()
      } finally {
        blocks.remove(crumb)
      }
      return blocks.isNotEmpty()
    }
  }

  private val core = hashMapOf<Any, Context>()

  private fun lookup(key: Any): Context {
    if (!core.containsKey(key))
      core[key] = Context()
    return core[key]!!
  }

  fun throttle(key: Any, millis: Long = 0): Boolean {
    return lookup(key).since <= millis || lookup(key).invoke()
  }

  fun block(key: Any, cb: () -> Unit) = lookup(key).invoke(cb)

  fun softTry(key: Any, cb: () -> Unit) {
    if (lookup(key).invoke()) cb()
  }
}