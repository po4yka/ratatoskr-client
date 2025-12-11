package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.IOSPlatform
import com.po4yka.bitesizereader.Platform
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.IosSecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.FileSaver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

/**
 * iOS platform module using DSL.
 *
 * Note: We use Koin DSL instead of annotations for platform modules because
 * KSP generates code to target-specific directories (iosArm64Main, etc.),
 * not to intermediate source sets (iosMain). The generated .module extension
 * is not visible from iosMain source set.
 */
val iosPlatformModule =
    module {
        single<DatabaseDriverFactory> { DatabaseDriverFactory() }
        single<SecureStorage> { IosSecureStorage() }
        single<HttpClientEngine> { Darwin.create() }
        single<FileSaver> { FileSaver() }
        single<Platform> { IOSPlatform() }
    }
