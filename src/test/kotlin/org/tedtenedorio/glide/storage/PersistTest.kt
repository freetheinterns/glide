package org.tedtenedorio.glide.storage

import kotlinx.serialization.Serializable
import org.junit.Before
import org.junit.Test
import org.tedtenedorio.glide.storage.Persist.jsonString
import org.tedtenedorio.glide.storage.Persist.load
import org.tedtenedorio.glide.storage.Persist.save
import org.tedtenedorio.glide.storage.serialization.JSON
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PersistTest {
  @Serializable
  private data class TestingClass(
    val value: String = "test",
    override val version: Int = 1
  ) : Versionable

  private lateinit var obj: TestingClass

  @Before
  fun setUp() {
    obj = TestingClass(value = "example")
  }

  @Test
  fun testPersistableIO() {
    save(obj, TestingClass.serializer())
    val other = load(TestingClass(), TestingClass.serializer())

    assertEquals(obj, other)
    assertNotEquals(TestingClass(), other)
  }

  @Test
  fun testPersistableVersionEnforcement() {
    save(JSON.parse(
      TestingClass.serializer(),
      obj.jsonString(TestingClass.serializer()).replace(
        "\"version\": ${obj.version}",
        "\"version\": ${obj.version + 1}"
      )
    ), TestingClass.serializer())

    val other = load(TestingClass(), TestingClass.serializer())

    assertNotEquals(obj, other)
    assertEquals(TestingClass(), other)
  }
}