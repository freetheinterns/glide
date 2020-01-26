package org.tedtenedorio.glide.storage

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.junit.Test
import org.tedtenedorio.glide.FILE_SIZES
import org.tedtenedorio.glide.storage.serialization.JSON
import kotlin.test.assertEquals

class StorageTest {
  @Serializable data class TT(val aaa: String = "3") : Persistable<TT> {
    override val version: Int = 1
    @Transient override var serializer = serializer()
  }

  @Test
  fun testPersistableSerialization() {
    val a = TT(aaa = "example")
    val b = JSON.parse(TT.serializer(), a.jsonString)
    assertEquals(a, b)
  }

  @Test
  fun testIoMemoizer() {
    FILE_SIZES["3"] = 4
    assertEquals(4, FILE_SIZES["3"]!!)
    println()
  }
}