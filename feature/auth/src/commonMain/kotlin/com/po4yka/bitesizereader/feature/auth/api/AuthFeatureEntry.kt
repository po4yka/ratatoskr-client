package com.po4yka.bitesizereader.feature.auth.api

import com.po4yka.bitesizereader.di.authEntry as internalAuthEntry
import com.po4yka.bitesizereader.navigation.AuthEntry
import org.koin.core.Koin

fun authFeatureEntry(koin: Koin): AuthEntry = internalAuthEntry(koin)
