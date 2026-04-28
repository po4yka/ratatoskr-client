package com.po4yka.ratatoskr.feature.settings.api

import com.po4yka.ratatoskr.di.settingsRouteEntries as internalSettingsRouteEntries
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import org.koin.core.Koin

fun settingsNavigationEntries(
    koin: Koin,
    digestMainRoute: () -> AppRoute,
): List<MainRouteEntry> = internalSettingsRouteEntries(koin, digestMainRoute)
