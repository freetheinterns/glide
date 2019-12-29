package common.glide.storage

import common.glide.storage.Persistable.Companion.update
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class StorageTest {

  @Test
  fun testCopyParity() {
    val vmap = object : FileMap() {
      override val filename: String = "testfile.txt"
    }

    val start = 6
    val end = 7

    vmap.update {
      this[":"] = start
    }
    vmap.mapData[":"] = end
    assertEquals(end, vmap[":"]!!)

    vmap.load()
    assertEquals(start, vmap[":"]!!)

    File(vmap.filename).deleteRecursively()
  }

  @Test
  fun testIoMemoizer() {
    IOMemoizer["3"] = 4
    assertEquals(4, IOMemoizer["3"]!!)
  }
}