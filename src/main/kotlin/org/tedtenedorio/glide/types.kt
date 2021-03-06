package org.tedtenedorio.glide

typealias Block = () -> Unit
typealias Loader<T> = () -> T
typealias Extension<T> = T.() -> Unit
typealias ExtOperation<E, T> = E.(T) -> Unit
typealias Operation<T> = (T) -> Unit
