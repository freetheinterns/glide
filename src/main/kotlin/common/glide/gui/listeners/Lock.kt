package common.glide.gui.listeners

import common.glide.Block
import common.glide.utils.MaximizingProperty

data class Lock(private val key: Any) {
  private class Signature {
    var invokedAt by MaximizingProperty(0L)
    override fun toString(): String = "Lock.Signature(invokedAt=$invokedAt)"
  }

  private val context: Signature
    get() = GLOBAL_LOCKS.getValue(key).also { GLOBAL_LOCKS[key] = it }

  fun throttle(millis: Long, block: Block) = context.let {
    if (NOW - it.invokedAt >= millis) {
      it.invokedAt = NOW
      try {
        block()
      } catch (exc: Exception) {
        exc.printStackTrace()
      }
    }
  }

  companion object {
    private val GLOBAL_LOCKS = hashMapOf<Any, Signature>().withDefault { Signature() }
    private val NOW: Long
      get() = System.currentTimeMillis()
  }
}