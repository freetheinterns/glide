package org.tedtenedorio.glide.extensions

import org.slf4j.Logger

fun Logger.trace(throwable: Throwable, message: (Throwable) -> String) {
  when {
    !isTraceEnabled -> return
    else -> trace(message(throwable), throwable)
  }
}

fun Logger.trace(message: () -> String) {
  when {
    !isTraceEnabled -> return
    else -> trace(message())
  }
}

fun Logger.debug(throwable: Throwable, message: (Throwable) -> String) {
  when {
    !isDebugEnabled -> return
    else -> debug(message(throwable), throwable)
  }
}

fun Logger.debug(message: () -> String) {
  when {
    !isDebugEnabled -> return
    else -> debug(message())
  }
}

fun Logger.info(throwable: Throwable, message: (Throwable) -> String) {
  when {
    !isInfoEnabled -> return
    else -> info(message(throwable), throwable)
  }
}

fun Logger.info(message: () -> String) {
  when {
    !isInfoEnabled -> return
    else -> info(message())
  }
}

fun Logger.warn(throwable: Throwable, message: (Throwable) -> String) {
  when {
    !isWarnEnabled -> return
    else -> warn(message(throwable), throwable)
  }
}

fun Logger.warn(message: () -> String) {
  when {
    !isWarnEnabled -> return
    else -> warn(message())
  }
}

fun Logger.error(throwable: Throwable, message: (Throwable) -> String) {
  when {
    !isErrorEnabled -> return
    else -> error(message(throwable), throwable)
  }
}

fun Logger.error(message: () -> String) {
  when {
    !isErrorEnabled -> return
    else -> error(message())
  }
}
