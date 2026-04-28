package com.po4yka.ratatoskr.feature.digest.navigation

import com.po4yka.ratatoskr.navigation.AppRoute

object DigestRoutes {
    const val FEATURE_ID = "digest"
    const val SCREEN_MAIN = "main"
    const val SCREEN_CUSTOM_CREATE = "custom-create"
    const val SCREEN_CUSTOM_VIEW = "custom-view"

    fun main(): AppRoute = AppRoute(FEATURE_ID, SCREEN_MAIN)

    fun customCreate(): AppRoute = AppRoute(FEATURE_ID, SCREEN_CUSTOM_CREATE)

    fun customView(digestId: String): AppRoute = AppRoute(FEATURE_ID, SCREEN_CUSTOM_VIEW, digestId)
}
