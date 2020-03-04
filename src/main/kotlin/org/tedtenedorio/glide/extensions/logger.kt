package org.tedtenedorio.glide.extensions

import org.slf4j.Logger

fun Logger.trace(throwable: Throwable? = null, message: () -> String) {
  when {
    !isTraceEnabled -> return
    throwable != null -> trace(message(), throwable)
    else -> trace(message())
  }
}

fun Logger.debug(throwable: Throwable? = null, message: () -> String) {
  when {
    !isDebugEnabled -> return
    throwable != null -> debug(message(), throwable)
    else -> debug(message())
  }
}

fun Logger.info(throwable: Throwable? = null, message: () -> String) {
  when {
    !isInfoEnabled -> return
    throwable != null -> info(message(), throwable)
    else -> info(message())
  }
}

fun Logger.warn(throwable: Throwable? = null, message: () -> String) {
  when {
    !isWarnEnabled -> return
    throwable != null -> warn(message(), throwable)
    else -> warn(message())
  }
}

fun Logger.error(throwable: Throwable? = null, message: () -> String) {
  when {
    !isErrorEnabled -> return
    throwable != null -> error(message(), throwable)
    else -> error(message())
  }
}
