/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package glide

import glide.gui.Launcher
import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
  @Test
  fun testAppHasAGreeting() {
    val classUnderTest = Launcher()
    assertNotNull(classUnderTest, "app should boot")
  }
}