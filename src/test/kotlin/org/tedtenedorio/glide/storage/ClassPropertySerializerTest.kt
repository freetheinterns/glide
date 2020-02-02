package org.tedtenedorio.glide.storage

import org.junit.Test
import org.tedtenedorio.glide.storage.serialization.ClassPropertySerializer
import org.tedtenedorio.glide.storage.serialization.ColorSerializer
import org.tedtenedorio.glide.storage.serialization.DisplayModeSerializer
import org.tedtenedorio.glide.storage.serialization.JSON
import org.tedtenedorio.glide.storage.serialization.RegexSerializer
import java.awt.Color
import java.awt.DisplayMode
import kotlin.test.assertEquals

class ClassPropertySerializerTest {
  data class InternalTestingClass(val num: Int) {
    val expected: String = """
      {
          "num": $num
      }
    """.trimIndent()
  }

  object TestClassPropertySerializer : ClassPropertySerializer<InternalTestingClass>() {
    override val className = "TestingClass"
    override val properties = listOf(InternalTestingClass::num)
    override val classConstructor = ::InternalTestingClass
  }

  @Test
  fun testGenericSerialization() {
    val obj = InternalTestingClass(4)
    val text = JSON.stringify(TestClassPropertySerializer, obj)

    assertEquals(obj.expected, text)

    val obj2 = JSON.parse(TestClassPropertySerializer, text)
    assertEquals(obj.num, obj2.num)
  }

  @Test
  fun testColorSerialization() {
    val obj = Color(1, 2, 3, 4)
    val text = JSON.stringify(ColorSerializer, obj)
    println(text)
    val obj2 = JSON.parse(ColorSerializer, text)
    assertEquals(obj.red, obj2.red)
    assertEquals(obj.green, obj2.green)
    assertEquals(obj.blue, obj2.blue)
    assertEquals(obj.alpha, obj2.alpha)
  }

  @Test
  fun testRegexSerialization() {
    val obj = Regex("123.+11", RegexOption.IGNORE_CASE)
    val text = JSON.stringify(RegexSerializer, obj)
    val obj2 = JSON.parse(RegexSerializer, text)
    assertEquals(obj.options, obj2.options)
    assertEquals(obj.pattern, obj2.pattern)
  }

  @Test
  fun testDisplayModeSerialization() {
    val obj = DisplayMode(1, 2, 3, 4)
    val text = JSON.stringify(DisplayModeSerializer, obj)
    val obj2 = JSON.parse(DisplayModeSerializer, text)
    assertEquals(obj.width, obj2.width)
    assertEquals(obj.height, obj2.height)
    assertEquals(obj.bitDepth, obj2.bitDepth)
    assertEquals(obj.refreshRate, obj2.refreshRate)
  }
}