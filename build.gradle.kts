plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM
  kotlin("jvm").version("1.3.10")

  // Apply the application to add support for building a CLI application
  application
}


repositories {
  jcenter()
}

dependencies {
  compileOnly(gradleKotlinDsl())

  compile(kotlin("gradle-plugin"))
  compile(kotlin("stdlib-jdk8"))
  compile(kotlin("reflect"))

  // Use the Kotlin test library
  testImplementation("org.jetbrains.kotlin:kotlin-test")

  // Use the Kotlin JUnit integration
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  // Define the main class for the application
  mainClassName = "glide.MainKt"
}
