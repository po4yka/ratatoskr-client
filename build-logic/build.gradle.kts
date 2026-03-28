plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:9.0.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    compileOnly("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.10.2")
}
