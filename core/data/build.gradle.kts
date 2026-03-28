plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("bitesize.kmp.library")
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.wire)
    alias(libs.plugins.kover)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.encoding)
            implementation(libs.wire.runtime)
            implementation(libs.wire.grpc.client)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitive.adapters)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlin.logging)
            implementation(libs.slf4j.api)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
            implementation(libs.tink.android)
            implementation(libs.datastore.preferences)
            implementation(libs.logback.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }

        named("desktopMain").dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.logback.classic)
            implementation(libs.multiplatform.settings.test)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.turbine)
            implementation(libs.multiplatform.settings.test)
        }

        androidUnitTest.dependencies {
            implementation(libs.mockk)
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.po4yka.bitesizereader.database")
            srcDirs.setFrom("src/commonMain/sqldelight")
        }
    }
}

wire {
    kotlin {
        explicitStreamingCalls = true
    }
    sourcePath {
        srcDir("src/commonMain/proto")
    }
}
