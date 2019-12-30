package common.glide.utils

import common.glide.utils.extensions.coerceMaximum
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class UtilsTest {
  @Test
  fun testCoerceMaximumDelegate() {
    val initial = 0L
    val end = 5L
    assertTrue(initial < end)

    var a by coerceMaximum { initial }

    assertEquals(initial, a)
    a = end
    assertEquals(end, a)
    a = initial // should do nothing
    assertEquals(end, a)
  }
}