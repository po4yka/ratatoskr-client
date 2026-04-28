package com.po4yka.ratatoskr.feature.collections.api

import com.po4yka.ratatoskr.di.collectionsRouteEntries as internalCollectionsRouteEntries
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import org.koin.core.Koin

fun collectionsNavigationEntries(
    koin: Koin,
    summaryDetailRoute: (String) -> AppRoute,
): List<MainRouteEntry> = internalCollectionsRouteEntries(koin, summaryDetailRoute)
