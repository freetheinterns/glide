package common.glide.async

import common.glide.utils.extensions.coerceMaximum
import common.glide.utils.extensions.currentThread

data class Lock(private val key: Any) {
  private data class Signature(val blocks: HashSet<Any> = hashSetOf()) {
    var invokedAt by coerceMaximum<Long> { 0 }
  }

  private val context: Signature
    get() = GLOBAL_LOCKS.getValue(key)

  val lockedThreadName: String
    get() = "Locked Execution of $key"
  val isLocked: Boolean
    get() = context.blocks.isNotEmpty()

  fun throttle(millis: Long, block: () -> Unit) = context.let {
    if (NOW - it.invokedAt <= millis || it.blocks.isNotEmpty())
      runLocked(block)
  }

  fun runLocked(block: () -> Unit) {
    context.apply {
      invokedAt = NOW
      if (blocks.isNotEmpty()) return

      val crumb: Double = Math.random()
      blocks.add(crumb)
      try {
        val ct = currentThread
        val currentName = ct.name
        ct.name = lockedThreadName
        block()
        ct.name = currentName

      } catch (err: Exception) {
        println(err.stackTrace.toString())
        err.printStackTrace()
      } finally {
        blocks.remove(crumb)
      }
    }
  }

  companion object {
    private val GLOBAL_LOCKS = hashMapOf<Any, Signature>().withDefault { Signature() }
    private val NOW: Long
      get() = System.currentTimeMillis()
  }
}