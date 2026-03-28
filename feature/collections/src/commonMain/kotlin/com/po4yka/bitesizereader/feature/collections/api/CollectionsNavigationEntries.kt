package com.po4yka.bitesizereader.feature.collections.api

import com.po4yka.bitesizereader.di.collectionsRouteEntries as internalCollectionsRouteEntries
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import org.koin.core.Koin

fun collectionsNavigationEntries(
    koin: Koin,
    summaryDetailRoute: (String) -> AppRoute,
): List<MainRouteEntry> = internalCollectionsRouteEntries(koin, summaryDetailRoute)
