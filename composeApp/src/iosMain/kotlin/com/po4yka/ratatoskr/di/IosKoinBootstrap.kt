package com.po4yka.ratatoskr.di

/**
 * Swift-visible DI bootstrap for the iOS host app.
 *
 * Top-level Kotlin functions and generated module accessors are brittle from Swift,
 * so the host uses this explicit wrapper instead.
 */
class IosKoinBootstrap {
    fun start() {
        initKoin(
            configuration = PlatformConfiguration(),
            modules = appModules(),
            appDeclaration = {},
            extraModules = emptyList(),
        )
    }
}
