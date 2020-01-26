package org.tedtenedorio.glide.storage

import org.junit.Test
import kotlin.test.assertEquals

class CacheableTest {
  private class TestingClass(override var priority: Int = 0) : Cacheable {
    override val byteSize: Long = 1
    override fun clear() {}
  }

  @Test
  fun testMemoizedManagement() {
    TestingClass().memoize()
    TestingClass().memoize()
    TestingClass(1).memoize()
    TestingClass(2).memoize()

    assertEquals(4, Cacheable.queueSize)
    Cacheable.manageGlobalCache(3)
    assertEquals(3, Cacheable.queueSize)
  }
}