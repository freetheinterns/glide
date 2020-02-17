package org.tedtenedorio.glide.extensions

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject


///////////////////////////////////////
// Logging Extensions
///////////////////////////////////////

// unwrap companion class to enclosing class given a Java Class
fun <T> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
  return ofClass.enclosingClass?.takeIf {
    ofClass.enclosingClass.kotlin.companionObject?.java == ofClass
  } ?: ofClass
}

// unwrap companion class to enclosing class given a Kotlin Class
fun <T : Any> unwrapCompanionClass(ofClass: KClass<T>): KClass<*> {
  return unwrapCompanionClass(ofClass.java).kotlin
}

fun <T : Any> T.logger(): Lazy<Logger> = lazy {
  Logger.getLogger(unwrapCompanionClass(this::class).simpleName)
}

val LogRecord.throwable: String
  get() {
    thrown ?: return ""
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    pw.println()
    thrown.printStackTrace(pw)
    pw.close()
    return sw.toString()
  }

fun <T> retry(times: Int = 3, block: () -> T): T {
  repeat(times - 1) {
    try {
      return block()
    } catch (exc: Exception) {
      println("Exception thrown in repeat block #$it")
      exc.printStackTrace()
    }
  }
  return block()
}