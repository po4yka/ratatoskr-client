package com.po4yka.bitesizereader.feature.summary.api

import com.po4yka.bitesizereader.di.summaryRouteEntries as internalSummaryRouteEntries
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import org.koin.core.Koin

fun summaryNavigationEntries(
    koin: Koin,
    digestCreateRoute: () -> AppRoute,
): List<MainRouteEntry> = internalSummaryRouteEntries(koin, digestCreateRoute)
