package org.tedtenedorio.glide

import java.lang.management.ManagementFactory
import java.security.SecureRandom
import kotlin.experimental.and


fun gcCount(): Long {
  var value = 0L
  for (bean in ManagementFactory.getGarbageCollectorMXBeans())
    bean.collectionCount.let { if (it != -1L) value += it }
  return value
}

fun superGC(): Long {
  val count = gcCount()
  System.gc()
  while (gcCount() == count);
  return ManagementFactory.getMemoryMXBean().let { it.nonHeapMemoryUsage.used + it.heapMemoryUsage.used }
}

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomAlphanumeric(length: Int): String {
  val random = SecureRandom()
  val bytes = ByteArray(length)
  random.nextBytes(bytes)

  return bytes.indices.map {
    charPool[(bytes[it] and 0xFF.toByte() and (charPool.size - 1).toByte()).toInt()]
  }.joinToString("")
}