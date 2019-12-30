// user guide at https://docs.gradle.org/5.0/userguide/multi_project_builds.html
pluginManagement {
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "kotlinx-serialization") {
        useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
      }
    }
  }

  repositories {
    gradlePluginPortal()
    maven("https://kotlin.bintray.com/kotlinx")
  }
}

rootProject.name = "glide"