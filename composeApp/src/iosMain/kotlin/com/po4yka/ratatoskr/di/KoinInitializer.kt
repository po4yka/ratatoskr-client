package com.po4yka.ratatoskr.di

import com.po4yka.ratatoskr.util.config.AppConfig
import kotlin.native.Platform
import org.koin.core.KoinApplication
import org.koin.core.module.Module

/**
 * iOS platform configuration placeholder.
 */
actual class PlatformConfiguration actual constructor()

actual fun platformModules(configuration: PlatformConfiguration): List<Module> = listOf(iosPlatformModule)

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    // Force AppConfig.Api.loggingEnabled to be unreadable as `true` on App Store /
    // TestFlight builds even if a future Swift→Kotlin bridge feeds a `true` value
    // through from Config.xcconfig or AppConfiguration.swift.
    AppConfig.Api.isReleaseBuild = !Platform.isDebugBinary
}
