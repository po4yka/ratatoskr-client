package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.feature.digest.navigation.DigestRoutes
import com.po4yka.bitesizereader.feature.digest.ui.screens.CustomDigestCreateScreen
import com.po4yka.bitesizereader.feature.digest.ui.screens.CustomDigestViewScreen
import com.po4yka.bitesizereader.feature.digest.ui.screens.DigestScreen
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultCustomDigestCreateComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultCustomDigestViewComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultDigestComponent
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import org.koin.core.Koin
import org.koin.dsl.module

val digestFeatureBindingsModule =
    module {
        factory {
            DigestViewModel(
                getDigestChannelsUseCase = get(),
                manageDigestSubscriptionUseCase = get(),
                getDigestPreferencesUseCase = get(),
                updateDigestPreferencesUseCase = get(),
                getDigestHistoryUseCase = get(),
                triggerDigestUseCase = get(),
            )
        }
        factory {
            CustomDigestCreateViewModel(
                summaryFeedPort = get(),
                createCustomDigestUseCase = get(),
            )
        }
        factory {
            CustomDigestViewViewModel(
                getCustomDigestByIdUseCase = get(),
                deleteCustomDigestUseCase = get(),
                repository = get(),
            )
        }
    }

fun digestRouteEntries(koin: Koin): List<MainRouteEntry> =
    listOf(
        DigestRouteEntry(viewModelFactory = { koin.get<DigestViewModel>() }),
        CustomDigestCreateRouteEntry(viewModelFactory = { koin.get<CustomDigestCreateViewModel>() }),
        CustomDigestViewRouteEntry(viewModelFactory = { koin.get<CustomDigestViewViewModel>() }),
    )

private class DigestRouteEntry(
    private val viewModelFactory: () -> DigestViewModel,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == DigestRoutes.FEATURE_ID && it.screenId == DigestRoutes.SCREEN_MAIN
        }?.let {
            val digestComponent =
                DefaultDigestComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    onBack = navigator::goBack,
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = digestComponent,
                render = { DigestScreen(component = digestComponent) },
            )
        }
}

private class CustomDigestCreateRouteEntry(
    private val viewModelFactory: () -> CustomDigestCreateViewModel,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == DigestRoutes.FEATURE_ID && it.screenId == DigestRoutes.SCREEN_CUSTOM_CREATE
        }?.let {
            val customDigestCreateComponent =
                DefaultCustomDigestCreateComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    onBack = navigator::goBack,
                    onDigestCreated = { digestId ->
                        navigator.goBack()
                        navigator.open(DigestRoutes.customView(digestId))
                    },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = customDigestCreateComponent,
                render = { CustomDigestCreateScreen(component = customDigestCreateComponent) },
            )
        }
}

private class CustomDigestViewRouteEntry(
    private val viewModelFactory: () -> CustomDigestViewViewModel,
) : MainRouteEntry {
    override val tab: MainTab? = null
    override val defaultRoute: AppRoute? = null

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == DigestRoutes.FEATURE_ID && it.screenId == DigestRoutes.SCREEN_CUSTOM_VIEW
        }?.let {
            val customDigestViewComponent =
                DefaultCustomDigestViewComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    digestId = requireNotNull(route.argument),
                    onBack = navigator::goBack,
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = customDigestViewComponent,
                render = { CustomDigestViewScreen(component = customDigestViewComponent) },
            )
        }
}
