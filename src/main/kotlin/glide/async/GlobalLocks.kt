package glide.async

import glide.utils.extensions.always
import glide.utils.extensions.currentThread

object GlobalLocks {
  val now by always { System.currentTimeMillis() }

  private class Context(private val key: Any) {
    private val blocks = hashSetOf<Any>()
    private var invokedAt: Long = 0
      set(value) {
        field = if (value > field) value else field
      }

    val since by always { now - invokedAt }
    val isBlocked by always { blocks.isNotEmpty() }

    fun runLocked(callback: () -> Unit) {
      invokedAt = now
      if (isBlocked) return

      val crumb: Double = Math.random()
      blocks.add(crumb)
      try {
        val ct = currentThread
        val currentName = ct.name
        ct.name = "Locked Execution of $key"
        callback()
        ct.name = currentName

      } catch (err: Exception) {
        println(err.stackTrace.toString())
        err.printStackTrace()
      } finally {
        blocks.remove(crumb)
      }
    }
  }

  private val core = hashMapOf<Any, Context>()

  private fun lookup(key: Any): Context =
    core[key] ?: Context(key).also { core[key] = it }

  fun throttle(key: Any, millis: Long = 0): Boolean =
    lookup(key).since <= millis || lookup(key).isBlocked

  fun runLocked(key: Any, callback: () -> Unit) =
    lookup(key).runLocked(callback)

  fun isLocked(key: Any): Boolean =
    lookup(key).isBlocked
}