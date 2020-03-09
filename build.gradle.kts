plugins {
  // Apply the Kotlin JVM & serialization plugins
  kotlin("jvm").version("1.3.70")
  id("kotlinx-serialization").version("1.3.70")

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
  implementation("org.slf4j:slf4j-log4j12:1.7.30")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
  implementation("org.openjdk.jmh:jmh-core:1.22")
  implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.4.3")
  implementation("com.charleskorn.kaml:kaml:0.16.1")
  // Use the Kotlin test library
  testImplementation("junit:junit:4.11")
  testImplementation(kotlin("test-junit"))
}

application {
  // Define the main class for the application
  mainClassName = "org.tedtenedorio.glide.MainKt"
}

tasks {
  test {
    dependsOn("cleanTest")
    failFast = false
    testLogging.showExceptions = true
  }
}
