package com.po4yka.ratatoskr.feature.digest.api

import com.po4yka.ratatoskr.di.digestRouteEntries as internalDigestRouteEntries
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import org.koin.core.Koin

fun digestNavigationEntries(koin: Koin): List<MainRouteEntry> = internalDigestRouteEntries(koin)
