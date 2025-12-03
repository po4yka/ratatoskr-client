package com.po4yka.bitesizereader.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module

actual class PlatformConfiguration actual constructor(val appContext: Context? = null)

actual fun platformModules(configuration: PlatformConfiguration): List<Module> =
    listOf(androidModule)

actual fun KoinApplication.platformExtras(configuration: PlatformConfiguration) {
    val context = requireNotNull(configuration.appContext) { "Android context is required for Koin." }
    androidContext(context)
}
