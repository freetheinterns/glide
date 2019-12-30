package common.glide.storage

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
  fun testIoMemoizer() {
    FILE_SIZES["3"] = 4
    assertEquals(4, FILE_SIZES["3"]!!)
    println(TT().jsonString)
  }
}