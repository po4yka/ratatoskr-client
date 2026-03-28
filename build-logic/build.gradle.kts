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
    implementation("org.jlleitschuh.gradle:ktlint-gradle:14.2.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
}
