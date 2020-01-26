package org.tedtenedorio.glide.utils

import org.junit.Assert.assertTrue
import org.junit.Test
import org.tedtenedorio.glide.BLACKHOLE
import org.tedtenedorio.glide.utils.CachedProperty.Companion.cache
import org.tedtenedorio.glide.utils.CachedProperty.Companion.invalidate
import java.lang.management.ManagementFactory
import kotlin.math.abs
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
        ::bat.invalidate(this)
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

  private fun gcCount(): Long {
    var value = 0L
    for (bean in ManagementFactory.getGarbageCollectorMXBeans())
      bean.collectionCount.let { if (it != -1L) value += it }
    return value
  }

  private fun superGC(): Long {
    val count = gcCount()
    System.gc()
    while (gcCount() == count);
    return ManagementFactory.getMemoryMXBean().let { it.nonHeapMemoryUsage.used + it.heapMemoryUsage.used }
  }

  @Test
  fun testCachedInvalidationClearsMemory() {
    val part = "ospkdf'a12-123ij04 cakksdbnfoowepjpnaskjlnf0123iomn4r01ljkn 10nm12098-=30jni09fcoi3rn0oi1091lksd09iojjk"
    val center = "6519csdf6wsd5g1a6edg4w6j4sdf jhjdfcd4g9a6EsdG4sdf61GF6A1sdfDF9A1ddV9G1WEdGF6A9f189sg1943trssdfs191fa6"
    val count = 100
    val charCount = count * (part.length + center.length) - center.length
    val stringMemSize = 8 * (((charCount * 2) + 45) / 8)

    class TT {
      var bat: String by cache {
        Array(count) { part }.joinToString(center)
      }

      fun clear() {
        ::bat.invalidate(this)
      }
    }
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    BLACKHOLE.consume(superGC())
    val t = TT()
    val beforeHeap = superGC()
    assertEquals(charCount, t.bat.length)
    val loadedHeap = superGC()
    t.clear()
    val afterHeap = superGC()
    // Heap difference on load is withing 50% of expected increase
    assert(abs(loadedHeap - beforeHeap - stringMemSize) < stringMemSize / 2)
    // Heap after run is closer to heap before run that heap under load
    // This validates that the cache has really been invalidated and the object collected by GC.
    assert(abs(afterHeap - loadedHeap) > abs(afterHeap - beforeHeap))
  }
}