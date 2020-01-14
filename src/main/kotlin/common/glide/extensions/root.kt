package common.glide.extensions

import common.glide.Block
import common.glide.Loader
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.findAnnotation


///////////////////////////////////////
// Global Variables
///////////////////////////////////////



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

///////////////////////////////////////
// Generic Extensions
///////////////////////////////////////

/**
 * Convenience accessor for the toString function
 */
val <T : Any> T.string: String
  get() = this.toString()

fun <T : Comparable<T>> coerceMaximum(getter: Loader<T>) = object : ReadWriteProperty<Any?, T> {
  private var value: T = getter()

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    if (value > this.value)
      this.value = value
  }

  override fun getValue(thisRef: Any?, property: KProperty<*>): T {
    return value
  }
}


/**
 * An observable delegate function that triggers callBack when the observed property changes value.
 * Also satisfies the NotNull promise on access
 *
 * @param initialValue T? of the property
 * @param callBack () -> Unit called after the property changes value
 * @return ReadWriteProperty<Any?, T> that triggers callBack appropriately
 */
inline fun <reified T> blindObserver(
  initialValue: T? = null,
  crossinline callBack: Block
) = object : ReadWriteProperty<Any?, T> {
  private var value: T? = initialValue

  override fun getValue(thisRef: Any?, property: KProperty<*>): T {
    if (null is T) return value as T  // Short circuit null check if T is a nullable type
    return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
  }

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    val trigger = this.value != value
    this.value = value
    if (trigger) callBack()
  }
}

///////////////////////////////////////
// Reflection Extensions
///////////////////////////////////////


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scope(vararg val scopeKeys: String)

val <T : KCallable<*>> T.scopes: Array<out String>
  get() = this.findAnnotation<Scope>()?.scopeKeys ?: arrayOf()