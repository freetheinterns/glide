package org.tedtenedorio.glide.properties

import org.junit.Test
import org.tedtenedorio.glide.BLACKHOLE
import org.tedtenedorio.glide.Loader
import org.tedtenedorio.glide.properties.CachedProperty.Companion.cache
import org.tedtenedorio.glide.properties.CachedProperty.Companion.invalidate
import org.tedtenedorio.glide.randomAlphanumeric
import org.tedtenedorio.glide.superGC
import kotlin.test.assertEquals

class CachedPropertyTest {
  private class TestableCache(loader: Loader<String>) {
    var cachedValue: String by cache(loader)
  }

  @Test
  fun testCachedProperty() {
    var count = 0

    val obj = TestableCache {
      count++
      "running"
    }

    // Loading the property for the first time will invoke the cache
    assertEquals(0, count)
    assertEquals("running", obj.cachedValue)
    assertEquals(1, count)

    // Manually setting the property will not invoke the cache
    obj.cachedValue = "cat"
    assertEquals("cat", obj.cachedValue)
    assertEquals(1, count)

    // Invalidating the cache and loading the property will invoke the cache
    // and reset the property from the cache
    TestableCache::cachedValue.invalidate(obj)
    assertEquals("running", obj.cachedValue)
    assertEquals(2, count)
  }

  /**
   * This test is meant to validate that objects stored in a cached property
   * are collected by GC once the property has been invalidated.
   * This is expensive to run in CI and should only be run locally.
   */
  //@Test
  fun testCachedInvalidationClearsMemory() {
    val charCount = 10000
    val stringMemSize = 8 * ((charCount + 45) / 8)

    val obj =
      TestableCache { randomAlphanumeric(charCount) }

    // Measure heap during & after cache is loaded
    // Consume the heap size after GC 3 times to force the heap into a relatively stable state
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    assertEquals(charCount, obj.cachedValue.length)
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    val loadedHeap = superGC()
    TestableCache::cachedValue.invalidate(obj)
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    val afterHeap = superGC()

    assert(loadedHeap - afterHeap > stringMemSize) {
      "Expected change in heap size ${loadedHeap - afterHeap} to be greater than estimated string size $stringMemSize"
    }
  }
}