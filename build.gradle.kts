plugins {
  // Apply the Kotlin JVM & serialization plugins
  kotlin("jvm").version("1.3.61")
  id("kotlinx-serialization").version("1.3.61")

  // Apply the application to add support for building a CLI application
  application
}

repositories {
  jcenter()
  mavenCentral()
  maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
  compileOnly(gradleKotlinDsl())
  implementation(kotlin("gradle-plugin"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")

  // Use the Kotlin test library
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
}

application {
  // Define the main class for the application
  mainClassName = "common.glide.MainKt"
}