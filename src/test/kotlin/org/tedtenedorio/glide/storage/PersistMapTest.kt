package org.tedtenedorio.glide.storage

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistMapTest {
  @Serializable
  private data class TestingClass(
    override val data: HashMap<String, Pair<Int, Long>> = hashMapOf()
  ) : PersistableMap<String, Int> {
    @Transient
    override val timeToLive: Long = 50

    override fun write() {}
  }

  companion object {
    private const val KEY: String = "example"
    private const val VALUE: Int = 10
  }

  private lateinit var obj: TestingClass

  @Before
  fun setUp() {
    obj = TestingClass()
  }

  @Test
  fun testPersistableMap() {
    obj[KEY] = VALUE
    assertEquals(VALUE, obj[KEY])
  }

  @Test
  fun testPersistableMapTTL() {
    obj[KEY] = VALUE
    runBlocking { delay(obj.timeToLive + 10) }
    assertNull(obj[KEY])
  }
}