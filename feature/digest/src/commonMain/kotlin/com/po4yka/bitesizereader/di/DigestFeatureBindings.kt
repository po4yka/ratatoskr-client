package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainScreen
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultCustomDigestCreateComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultCustomDigestViewComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultDigestComponent
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestCreateViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.CustomDigestViewViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.DigestViewModel
import org.koin.dsl.bind
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
        single {
            val koin = getKoin()
            DigestRouteEntry(viewModelFactory = { koin.get<DigestViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            CustomDigestCreateRouteEntry(viewModelFactory = { koin.get<CustomDigestCreateViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            CustomDigestViewRouteEntry(viewModelFactory = { koin.get<CustomDigestViewViewModel>() })
        } bind MainRouteEntry::class
    }

private class DigestRouteEntry(
    private val viewModelFactory: () -> DigestViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.DIGEST
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.Digest)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultDigestComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        onBack = navigator::goBack,
                    ),
            )
        }
}

private class CustomDigestCreateRouteEntry(
    private val viewModelFactory: () -> CustomDigestCreateViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.CUSTOM_DIGEST_CREATE
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.CustomDigestCreate)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultCustomDigestCreateComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        onBack = navigator::goBack,
                        onDigestCreated = { digestId ->
                            navigator.goBack()
                            navigator.openCustomDigestView(digestId)
                        },
                    ),
            )
        }
}

private class CustomDigestViewRouteEntry(
    private val viewModelFactory: () -> CustomDigestViewViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.CUSTOM_DIGEST_VIEW
    override val tab: MainTab? = null
    override val defaultRoute: MainRoute? = null

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.CustomDigestView)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultCustomDigestViewComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        digestId = it.digestId,
                        onBack = navigator::goBack,
                    ),
            )
        }
}
