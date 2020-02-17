package org.tedtenedorio.glide.storage.serialization

import kotlin.reflect.KFunction2

object RegexSerializer : ClassPropertySerializer<Regex>() {
  override val classConstructor: KFunction2<String, Set<RegexOption>, Regex> = ::Regex
  override val properties = listOf(
    Regex::pattern,
    Regex::options
  )
}