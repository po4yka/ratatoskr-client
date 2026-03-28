package com.po4yka.bitesizereader.app

import com.po4yka.bitesizereader.domain.usecase.GetProxiedImageUrlUseCase
import com.po4yka.bitesizereader.feature.auth.api.AuthSessionPort
import com.po4yka.bitesizereader.feature.auth.api.authFeatureEntry
import com.po4yka.bitesizereader.feature.collections.api.collectionsNavigationEntries
import com.po4yka.bitesizereader.feature.digest.navigation.DigestRoutes
import com.po4yka.bitesizereader.feature.digest.api.digestNavigationEntries
import com.po4yka.bitesizereader.feature.settings.api.settingsNavigationEntries
import com.po4yka.bitesizereader.feature.summary.api.summaryNavigationEntries
import com.po4yka.bitesizereader.feature.summary.navigation.SummaryRoutes
import org.koin.core.Koin

fun assembleAppCompositionRoot(koin: Koin): AppCompositionRoot =
    AppCompositionRoot(
        authRepository = koin.get<AuthSessionPort>(),
        authEntry = authFeatureEntry(koin),
        mainEntries =
            buildList {
                addAll(summaryNavigationEntries(koin, digestCreateRoute = DigestRoutes::customCreate))
                addAll(collectionsNavigationEntries(koin, summaryDetailRoute = SummaryRoutes::detail))
                addAll(settingsNavigationEntries(koin, digestMainRoute = DigestRoutes::main))
                addAll(digestNavigationEntries(koin))
            },
        imageUrlTransformer = koin.get<GetProxiedImageUrlUseCase>()::invoke,
    )
