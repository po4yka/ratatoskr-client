package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.feature.settings.navigation.SettingsRoutes
import com.po4yka.bitesizereader.feature.settings.ui.screens.SettingsScreen
import com.po4yka.bitesizereader.feature.settings.ui.screens.StatsScreen
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultSettingsComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultStatsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalController
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SyncSettingsDelegate
import com.po4yka.bitesizereader.presentation.viewmodel.StatsViewModel
import org.koin.core.Koin
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsFeatureBindingsModule =
    module {
        factory {
            SettingsViewModel(
                telegramDelegate = get(),
                syncDelegate = get(),
                accountDelegate = get(),
            )
        }
        factory {
            SyncSettingsDelegate(
                syncDataUseCase = get(),
                requestOpsPort = get(),
                contentCachePort = get(),
            )
        }
        factory {
            ReadingGoalViewModel(
                getReadingGoalProgressUseCase = get(),
                updateReadingGoalUseCase = get(),
                recalculateStreakUseCase = get(),
            )
        } bind ReadingGoalController::class
        factory {
            StatsViewModel(
                getUserStats = get(),
                userPreferencesRepository = get(),
            )
        }
    }

fun settingsRouteEntries(
    koin: Koin,
    digestMainRoute: () -> AppRoute,
): List<MainRouteEntry> =
    listOf(
        StatsRouteEntry(viewModelFactory = { koin.get<StatsViewModel>() }),
        SettingsRouteEntry(
            viewModelFactory = { koin.get<SettingsViewModel>() },
            readingGoalControllerFactory = { koin.get<ReadingGoalController>() },
            digestMainRoute = digestMainRoute,
        ),
    )

private class StatsRouteEntry(
    private val viewModelFactory: () -> StatsViewModel,
) : MainRouteEntry {
    override val tab: MainTab = MainTab.STATS
    override val defaultRoute: AppRoute = SettingsRoutes.stats()

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SettingsRoutes.FEATURE_ID && it.screenId == SettingsRoutes.SCREEN_STATS
        }?.let {
            val statsComponent =
                DefaultStatsComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = statsComponent,
                render = { StatsScreen(component = statsComponent) },
            )
        }
}

private class SettingsRouteEntry(
    private val viewModelFactory: () -> SettingsViewModel,
    private val readingGoalControllerFactory: () -> ReadingGoalController,
    private val digestMainRoute: () -> AppRoute,
) : MainRouteEntry {
    override val tab: MainTab = MainTab.SETTINGS
    override val defaultRoute: AppRoute = SettingsRoutes.settings()

    override fun create(
        route: AppRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        route.takeIf {
            it.featureId == SettingsRoutes.FEATURE_ID && it.screenId == SettingsRoutes.SCREEN_SETTINGS
        }?.let {
            val settingsComponent =
                DefaultSettingsComponent(
                    componentContext = componentContext,
                    viewModelFactory = viewModelFactory,
                    readingGoalControllerFactory = readingGoalControllerFactory,
                    onDigest = { navigator.open(digestMainRoute()) },
                )
            MainChildDescriptor(
                route = route,
                tab = tab,
                component = settingsComponent,
                render = { SettingsScreen(component = settingsComponent) },
            )
        }
}
