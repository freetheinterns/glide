package common.glide.extensions

import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.findAnnotation


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
// Reflection Extensions
///////////////////////////////////////


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scope(vararg val scopeKeys: String)

val <T : KCallable<*>> T.scopes: Array<out String>
  get() = this.findAnnotation<Scope>()?.scopeKeys ?: arrayOf()