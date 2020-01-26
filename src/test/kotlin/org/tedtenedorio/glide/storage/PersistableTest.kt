package org.tedtenedorio.glide.storage

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.tedtenedorio.glide.storage.serialization.JSON
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PersistableTest {
  @Serializable private data class TestingClass(
    val value: String = "test",
    override val version: Int = 1
  ) : Persistable<TestingClass> {
    @Transient override var serializer = serializer()
  }

  private lateinit var obj: TestingClass

  @Before
  fun setUp() {
    obj = TestingClass(value = "example")
  }

  @After
  fun tearDown() {
    File(obj.filename).deleteRecursively()
  }

  @Test
  fun testPersistableIO() {
    obj.save()
    val other = TestingClass().load()

    assertEquals(obj, other)
    assertNotEquals(TestingClass(), other)
  }

  @Test
  fun testPersistableVersionEnforcement() {
    JSON.parse(
      obj.serializer,
      obj.jsonString.replace(
        "\"version\": ${obj.version}",
        "\"version\": ${obj.version + 1}"
      )
    ).save()

    val other = TestingClass().load()

    assertNotEquals(obj, other)
    assertEquals(TestingClass(), other)
  }
}