package common.glide.utils

import common.glide.utils.CachedProperty.Companion.cache
import common.glide.utils.CachedProperty.Companion.invalidateCache
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class UtilsTest {
  @Test
  fun testCoerceMaximumDelegate() {
    val initial = 0L
    val end = 5L
    assertTrue(initial < end)

    var a by MaximizingProperty(initial)

    assertEquals(initial, a)
    a = end
    assertEquals(end, a)
    a = initial // should do nothing
    assertEquals(end, a)
  }

  @Test
  fun testCachedProperty() {
    var count = 0

    class TT {
      var bat: String by cache {
        count++
        "running"
      }

      fun clear() {
        invalidateCache(::bat)
      }
    }

    val t = TT()

    assert(count == 0)
    assert(t.bat == "running")
    assert(count == 1)
    t.bat = "cat"
    assert(t.bat == "cat")
    assert(count == 1)
    t.clear()
    assert(t.bat == "running")
    assert(count == 2)
  }
}