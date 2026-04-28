package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.IOSPlatform
import com.po4yka.ratatoskr.Platform
import com.po4yka.ratatoskr.data.local.DatabaseDriverFactory
import com.po4yka.ratatoskr.data.local.IosSecureStorage
import com.po4yka.ratatoskr.data.local.SecureStorage
import com.po4yka.ratatoskr.util.FileSaver
import com.po4yka.ratatoskr.util.network.IosNetworkMonitor
import com.po4yka.ratatoskr.util.network.NetworkMonitor
import com.po4yka.ratatoskr.util.share.IosShareManager
import com.po4yka.ratatoskr.util.share.ShareManager
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
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
        single<NetworkMonitor>(createdAtStart = false) {
            IosNetworkMonitor()
        }
        single<ShareManager> { IosShareManager() }
        single<ObservableSettings> { NSUserDefaultsSettings.Factory().create("reading_preferences") }
        single { com.po4yka.ratatoskr.util.audio.AudioPlayer() }
    }
