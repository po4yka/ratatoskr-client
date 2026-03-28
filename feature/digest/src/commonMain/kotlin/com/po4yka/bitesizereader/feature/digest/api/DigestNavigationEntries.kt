package com.po4yka.bitesizereader.feature.digest.api

import com.po4yka.bitesizereader.di.digestRouteEntries as internalDigestRouteEntries
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import org.koin.core.Koin

fun digestNavigationEntries(koin: Koin): List<MainRouteEntry> = internalDigestRouteEntries(koin)
