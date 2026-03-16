package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.DesktopPlatform
import com.po4yka.bitesizereader.Platform
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.DesktopSecureStorage
import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.util.FileSaver
import com.po4yka.bitesizereader.util.network.DesktopNetworkMonitor
import com.po4yka.bitesizereader.util.network.NetworkMonitor
import com.po4yka.bitesizereader.util.share.DesktopShareManager
import com.po4yka.bitesizereader.util.share.ShareManager
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

/**
 * Desktop platform module using DSL.
 *
 * Note: We use Koin DSL instead of annotations for platform modules because
 * KSP generates code to target-specific directories, not to intermediate
 * source sets (desktopMain). The generated .module extension is not visible
 * from desktopMain source set.
 */
val desktopPlatformModule =
    module {
        single<DatabaseDriverFactory> { DatabaseDriverFactory() }
        single<SecureStorage> { DesktopSecureStorage() }
        single<HttpClientEngine> { OkHttp.create() }
        single<FileSaver> { FileSaver() }
        single<Platform> { DesktopPlatform() }
        single<NetworkMonitor> { DesktopNetworkMonitor() }
        single<ShareManager> { DesktopShareManager() }
        single<ObservableSettings> { MapSettings() }
        single { com.po4yka.bitesizereader.util.audio.AudioPlayer() }
    }
