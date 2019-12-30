package common.glide.storage

import common.glide.slideshow.CachedImage
import common.glide.utils.extensions.Scope
import common.glide.utils.extensions.scopes
import kotlinx.serialization.Serializable
import kotlin.system.exitProcess


@Serializable
data class KeyBindings(
  val keyMap: Map<String, Map<Int, String>> = hashMapOf(
    "Projector" to hashMapOf(
      8 to "inchBackward",
      9 to "nextCatalog",
      10 to "pageForward",
      16 to "previousCatalog",
      27 to "exit",
      32 to "toggleSlideshow",
      37 to "pageBackward",
      38 to "inchForward",
      39 to "pageForward",
      40 to "inchBackward",
      79 to "changeScaling",
      127 to "deleteCatalog",
      115 to "archiveCatalog"
    ),
    "Launcher" to hashMapOf(
      10 to "launch",
      27 to "exit",
      39 to "pageForward"
    )
  )
) : Persistable<KeyBindings>(serializer()) {

  fun trigger(name: String) =
    this::class.members.forEach {
      if (it.name == name && it.scopes.contains(ENV.scope))
        it.call(this)
    }

  fun trigger(code: Int) = trigger(keyMap[ENV.scope]?.get(code) ?: "noBinding")

  fun options(scope: String = ENV.scope): List<String> =
    this::class.members.filter { it.scopes.contains(scope) }.map { it.name }

  @Scope("Launcher")
  fun launch() {
    ENV.launcher!!.launch()
  }

  @Scope("Projector", "Launcher")
  fun pageForward() = when (ENV.scope) {
    "Projector" -> ENV.projector!!.next()
    "Launcher"  -> ENV.launcher!!.nextCard()
    else        -> Unit
  }

  @Scope("Projector")
  fun pageBackward() {
    ENV.projector!!.prev()
  }

  @Scope("Projector")
  fun inchForward() {
    ENV.projector!!.dumbNext()
  }

  @Scope("Projector")
  fun inchBackward() {
    ENV.projector!!.previous()
  }

  @Scope("Projector")
  fun nextCatalog() {
    ENV.projector!!.nextFolder()
  }

  @Scope("Projector")
  fun previousCatalog() {
    ENV.projector!!.prevFolder()
  }

  @Scope("Projector")
  fun deleteCatalog() {
    ENV.projector!!.deleteCurrentDirectory()
  }

  @Scope("Projector")
  fun archiveCatalog() {
    ENV.projector!!.archiveCurrentDirectory()
  }

  @Scope("Projector")
  fun toggleSlideshow() {
    ENV.projector!!.toggleTimer()
  }

  @Scope("Projector", "Launcher")
  fun exit() = when (ENV.scope) {
    "Projector" -> ENV.projector!!.exit()
    "Launcher"  -> ENV.launcher!!.dispose().also { exitProcess(0) }
    else        -> Unit
  }

  @Scope("Projector")
  fun changeScaling() {
    ENV.projector!!.scaling = CachedImage.nextScalingOption()
  }
}