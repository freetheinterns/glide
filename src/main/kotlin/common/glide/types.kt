package common.glide

typealias Block = () -> Unit
typealias Loader<T> = () -> T
typealias Extension<T> = T.() -> Unit
typealias ExtOperation<E, T> = E.(T) -> Unit
typealias Operation<T> = (T) -> Unit
typealias Mutator<T, R> = (T) -> R
