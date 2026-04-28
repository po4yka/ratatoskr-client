package com.po4yka.ratatoskr.feature.summary.api

import com.po4yka.ratatoskr.di.summaryRouteEntries as internalSummaryRouteEntries
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import org.koin.core.Koin

fun summaryNavigationEntries(
    koin: Koin,
    digestCreateRoute: () -> AppRoute,
): List<MainRouteEntry> = internalSummaryRouteEntries(koin, digestCreateRoute)
