package com.po4yka.bitesizereader.feature.settings.api

import com.po4yka.bitesizereader.di.settingsRouteEntries as internalSettingsRouteEntries
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import org.koin.core.Koin

fun settingsNavigationEntries(
    koin: Koin,
    digestMainRoute: () -> AppRoute,
): List<MainRouteEntry> = internalSettingsRouteEntries(koin, digestMainRoute)
