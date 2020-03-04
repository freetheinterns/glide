package org.tedtenedorio.glide.properties

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> CoroutineScope.lazyAwait(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> T
): Lazy<T> {
  val jobContext = coroutineContext + context
  val job: Deferred<T> = async(jobContext, start, block)
  return lazy { runBlocking(jobContext) { job.await() } }
}