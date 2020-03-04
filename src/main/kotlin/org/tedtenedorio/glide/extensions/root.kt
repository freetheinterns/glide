package org.tedtenedorio.glide.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates.vetoable
import kotlin.properties.ReadWriteProperty
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
  LoggerFactory.getLogger(unwrapCompanionClass(this::class).simpleName)
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

inline fun <T> checks(
  value: T,
  crossinline block: (T) -> Boolean
): ReadWriteProperty<Any?, T> = vetoable(value) { _, _, next ->
  block(next).also {
    if (!it) {
      throw IllegalStateException("Check Failed")
    }
  }
}

inline fun <T> checks(
  value: T,
  crossinline block: (T) -> Boolean,
  noinline message: (T) -> Any
): ReadWriteProperty<Any?, T> = vetoable(value) { _, _, next ->
  block(next).also {
    if (!it) {
      throw IllegalStateException(message.invoke(next).toString())
    }
  }
}