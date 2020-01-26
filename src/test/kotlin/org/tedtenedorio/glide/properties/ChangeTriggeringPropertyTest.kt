package org.tedtenedorio.glide.properties

import org.junit.Test
import org.tedtenedorio.glide.properties.ChangeTriggeringProperty.Companion.blindObserver
import kotlin.test.assertEquals

class ChangeTriggeringPropertyTest {
  @Test
  fun testTriggeringProperty() {
    var count = 0
    var prop: Int by blindObserver(0) { count++ }

    assertEquals(0, count)
    assertEquals(0, prop)

    // Check that count invoked when property is changed
    prop = 1
    assertEquals(1, count)
    assertEquals(1, prop)


    // Check that nothing changes when value set is not different
    prop = 1
    assertEquals(1, count)
    assertEquals(1, prop)

    // Check that count invoked when property is changed
    prop = -1
    assertEquals(2, count)
    assertEquals(-1, prop)

  }
}