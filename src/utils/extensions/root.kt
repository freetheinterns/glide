package utils.extensions

import storage.ENV
import java.awt.DisplayMode
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberProperties


///////////////////////////////////////
// Global Variables
///////////////////////////////////////

const val CACHED_PATH = 0
const val CACHED_FILE = 1
const val CACHE_FULL_IMAGE = 2
const val CACHE_RESIZED_IMAGE = 3
val BEST_DISPLAY_MODES = arrayOf(
  DisplayMode(2560, 1440, 32, 0),
  DisplayMode(2560, 1440, 16, 0),
  DisplayMode(2560, 1440, 8, 0)
)


///////////////////////////////////////
// Global Functions
///////////////////////////////////////

/**
 * Runs System gc & finalization twice
 *
 * @param timeout Long for the milliseconds slept in-between each action
 */
fun superGC(timeout: Long = 100) {
  try {
    System.gc()
    Thread.sleep(timeout)
    System.runFinalization()
    Thread.sleep(timeout)
    System.gc()
    Thread.sleep(timeout)
    System.runFinalization()
  } catch (ex: InterruptedException) {
    ex.printStackTrace()
  }
}

val THROTTLE_MAP = hashMapOf<Any, Long>()

/**
 * Determines if the provided key has a record that occurred within the millisecondWindow time window
 *
 * @param key Any instance that will be used as a key to track events
 * @param millisecondWindow Long for the milliseconds since the last event that will indicate a hit
 * @return Boolean indicating if the millisecond window overlaps with the last recorded event
 */
fun shouldThrottle(key: Any, millisecondWindow: Long): Boolean {
  val now = System.currentTimeMillis()
  if (key !in THROTTLE_MAP) {
    THROTTLE_MAP[key] = now
    return false
  } else if (now - THROTTLE_MAP[key]!! < millisecondWindow) {
    return true
  }
  THROTTLE_MAP[key] = now
  return false
}

fun vprint(obj: Any?) = if (ENV.verbose) print(obj) else null
fun vprintln(obj: Any?) = if (ENV.verbose) println(obj) else null


///////////////////////////////////////
// Generic Extensions
///////////////////////////////////////

/**
 * Convenience accessor for the toString function
 */
val <T : Any> T.string: String
  get() = this.toString()

/**
 * Implements a cached property with an inline block
 *
 * @param cacheMiss () -> T called when property is fetched and cache is null
 * @return ReadWriteProperty<Any, T> that calls cacheMiss appropriately
 */
inline fun <T> lazyCache(crossinline cacheMiss: () -> T) = object : ReadWriteProperty<Any, T> {
  private var value: T? = null
    get() {
      if (field == null)
        field = cacheMiss()
      return field
    }

  override fun getValue(thisRef: Any, property: KProperty<*>): T {
    return value ?: throw AssertionError("Value set to null by another thread")
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
    this.value = value
  }
}

//
/**
 * An observable delegate function that triggers callBack when the observed property changes value
 *
 * @param initialValue T of the property
 * @param callBack () -> Unit called when the property changes value
 * @return ReadWriteProperty<Any?, T> that triggers callBack appropriately
 */
inline fun <T> blindObserver(
        initialValue: T,
        crossinline callBack: () -> Unit
): ReadWriteProperty<Any?, T> =
  Delegates.observable(initialValue) { _, old, new ->
    if (old != new) callBack()
  }

///////////////////////////////////////
// Reflection Extensions
///////////////////////////////////////

/**
 * @receiver T instance of Any
 * @return Collection<KProperty1<*, *>> all available properties for the instance
 */
val <T : Any> T.properties: Collection<KProperty1<*, *>>
  get() {
    val props = ArrayList<KProperty1<*, *>>()
    if (this is KClass<*>) {
      props.addAll(this.memberProperties)
    } else {
      props.addAll(this::class.memberProperties)
    }

    val companion = this::class.companionObject
    if (companion is KClass<*>) {
      props.addAll(companion.memberProperties)
    }
    return props
  }

/**
 * @receiver T instance of Any
 * @param name String matching the name of a property on T
 * @return Any? value stored on T in the property indicated by name
 */
fun <T : Any> T.getAttribute(name: String): Any? {
  val property = this::class.properties.find { it.name == name }
  if (property is KProperty1<*, *>) {
    return property.getter.call(this)
  } else {
    throw NotImplementedError("Could not find property named $name on instance of class: ${this::class.simpleName}")
  }
}

/**
 * @receiver T instance of Any
 * @param name String matching the name of a WRITABLE property on T
 * @param value Any? value to be written to
 */
fun <T : Any, U> T.setAttribute(name: String, value: U) {
  val property = this::class.properties.find { it.name == name }
  if (property is KMutableProperty<*>) {
    property.setter.call(this, value)
  } else {
    throw NotImplementedError("Could not find property named $name on instance of class: ${this::class.simpleName}")
  }
}