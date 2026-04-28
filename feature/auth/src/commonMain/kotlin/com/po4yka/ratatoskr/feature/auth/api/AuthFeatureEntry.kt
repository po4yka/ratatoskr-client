package com.po4yka.ratatoskr.feature.auth.api

import com.po4yka.ratatoskr.di.authEntry as internalAuthEntry
import com.po4yka.ratatoskr.navigation.AuthEntry
import org.koin.core.Koin

fun authFeatureEntry(koin: Koin): AuthEntry = internalAuthEntry(koin)
