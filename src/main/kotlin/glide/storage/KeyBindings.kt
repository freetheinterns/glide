package glide.storage

import glide.slideshow.CachedImage
import glide.utils.extensions.Scope
import glide.utils.extensions.scopes
import kotlin.system.exitProcess


object KeyBindings : FileMap() {

  fun trigger(name: String) =
    this::class.members.forEach {
      if (it.name == name && it.scopes.contains(ENV.scope))
        it.call(this)
    }

  fun triggerByCode(code: Int) = trigger(map[ENV.scope]?.get(code) ?: "noBinding")

  fun options(scope: String = ENV.scope): List<String> =
    this::class.members.filter { it.scopes.contains(scope) }.map { it.name }

  private val map by fileData {
    hashMapOf(
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
  }

  @Scope("Launcher")
  fun launch() {
    ENV.launcher!!.launch()
  }

  @Scope("Projector", "Launcher")
  fun pageForward() = when (ENV.scope) {
    "Projector" -> ENV.projector!!.next()
    "Launcher"  -> ENV.launcher!!.nextCard()
    else        -> {
    }
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
    "Launcher"  -> {
      ENV.launcher!!.dispose()
      exitProcess(0)
    }
    else        -> {
    }
  }

  @Scope("Projector")
  fun changeScaling() {
    ENV.projector!!.scaling = CachedImage.nextScalingOption()
  }
}
