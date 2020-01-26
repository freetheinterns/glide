package org.tedtenedorio.glide.properties

import org.junit.Test
import kotlin.test.assertEquals

class MaximizingPropertyTest {
  @Test
  fun testPropertyOnlyIncreases() {
    val initial = 0L
    val end = 5L
    var onlyIncreasing by MaximizingProperty(initial)

    assert(initial < end)
    assertEquals(initial, onlyIncreasing)

    // End is > onlyIncreasing so this should effectively change the value
    onlyIncreasing = end
    assertEquals(end, onlyIncreasing)

    // Initial is < onlyIncreasing now so this should do nothing
    onlyIncreasing = initial
    assertEquals(end, onlyIncreasing)
  }
}