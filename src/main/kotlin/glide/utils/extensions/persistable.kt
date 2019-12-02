package glide.utils.extensions

import glide.storage.Persistable
import java.io.File


fun <T : Persistable<*>> T.update(block: T.() -> Unit) {
  lock.runLocked {
    block()
    println("SAVING: ${this::class.simpleName}")
    File(filename).writeObject(toSerializedInstance())
  }
}