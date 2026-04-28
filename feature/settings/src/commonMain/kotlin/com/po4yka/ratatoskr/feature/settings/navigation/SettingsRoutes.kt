package com.po4yka.ratatoskr.feature.settings.navigation

import com.po4yka.ratatoskr.navigation.AppRoute

object SettingsRoutes {
    const val FEATURE_ID = "settings"
    const val SCREEN_STATS = "stats"
    const val SCREEN_SETTINGS = "settings"

    fun stats(): AppRoute = AppRoute(FEATURE_ID, SCREEN_STATS)

    fun settings(): AppRoute = AppRoute(FEATURE_ID, SCREEN_SETTINGS)
}
