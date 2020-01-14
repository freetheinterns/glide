package common.glide.storage

import common.glide.FILE_SIZES
import common.glide.storage.serialization.JSON
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals

class StorageTest {
  @Serializable
  data class TT(
    val aaa: Int = 3,
    val bbb: String = "cat"
  ) : Persistable<TT>(serializer())

  @Test
  fun testPersistableSerialization() {
    val a = TT(bbb = "example")
    val b = JSON.parse<TT>(TT.serializer(), a.jsonString)
    assertEquals(a, b)
  }

  @Test
  fun testIoMemoizer() {
    FILE_SIZES["3"] = 4
    assertEquals(4, FILE_SIZES["3"]!!)
    println()
  }
}