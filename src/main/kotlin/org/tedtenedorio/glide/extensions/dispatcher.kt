package org.tedtenedorio.glide.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.time.Duration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class IncrementingThreadFactory(private val name: String) : ThreadFactory {
  private var count = 0
  override fun newThread(r: Runnable): Thread = Thread(r, "$name-pool-t${++count}")
}

fun threadPoolDispatcher(
  coreThreads: Int = 2,
  maxThreads: Int = coreThreads,
  timeout: Duration = Duration.ZERO,
  queue: BlockingQueue<Runnable> = LinkedBlockingQueue(coreThreads),
  name: String
): CoroutineDispatcher = ThreadPoolExecutor(
  coreThreads,
  maxThreads,
  timeout.toMillis(),
  TimeUnit.MILLISECONDS,
  queue,
  IncrementingThreadFactory(name)
).asCoroutineDispatcher()