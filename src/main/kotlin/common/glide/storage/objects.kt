package common.glide.storage


val ENV by lazy { SlideshowSettings().load() }
val KEY_BINDINGS by lazy { KeyBindings().load() }
val FILE_SIZES by lazy { FileSizeMemoizer().load() }
