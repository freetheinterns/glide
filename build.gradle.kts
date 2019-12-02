plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
  kotlin("jvm").version("1.3.61")

  // Apply the application to add support for building a CLI application
  application
}


repositories {
  jcenter()
}

dependencies {
  compileOnly(gradleKotlinDsl())
  implementation(kotlin("gradle-plugin"))
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

  // Use the Kotlin test library
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
}

application {
  // Define the main class for the application
  mainClassName = "glide.MainKt"
}
