package org.tedtenedorio.glide.storage

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistableMapTest {
  @Serializable
  private data class TestingClass(
    override val data: HashMap<String, Pair<Int, Long>> = hashMapOf()
  ) : PersistableMap<String, Int, TestingClass> {
    @Transient
    override val timeToLive: Long = 50

    @Transient
    override val version: Int = 1

    @Transient
    override var serializer = serializer()
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

  @After
  fun tearDown() {
    File(obj.filename).deleteRecursively()
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