package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainScreen
import com.po4yka.bitesizereader.navigation.MainTab
import com.po4yka.bitesizereader.presentation.navigation.DefaultSettingsComponent
import com.po4yka.bitesizereader.presentation.navigation.DefaultStatsComponent
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalController
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SyncSettingsDelegate
import com.po4yka.bitesizereader.presentation.viewmodel.StatsViewModel
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
        single {
            val koin = getKoin()
            StatsRouteEntry(viewModelFactory = { koin.get<StatsViewModel>() })
        } bind MainRouteEntry::class
        single {
            val koin = getKoin()
            SettingsRouteEntry(
                viewModelFactory = { koin.get<SettingsViewModel>() },
                readingGoalControllerFactory = { koin.get<ReadingGoalController>() },
            )
        } bind MainRouteEntry::class
    }

private class StatsRouteEntry(
    private val viewModelFactory: () -> StatsViewModel,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.STATS
    override val tab: MainTab = MainTab.STATS
    override val defaultRoute: MainRoute = MainRoute.Stats

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.Stats)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultStatsComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                    ),
            )
        }
}

private class SettingsRouteEntry(
    private val viewModelFactory: () -> SettingsViewModel,
    private val readingGoalControllerFactory: () -> ReadingGoalController,
) : MainRouteEntry {
    override val screen: MainScreen = MainScreen.SETTINGS
    override val tab: MainTab = MainTab.SETTINGS
    override val defaultRoute: MainRoute = MainRoute.Settings

    override fun create(
        route: MainRoute,
        componentContext: com.arkivanov.decompose.ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor? =
        (route as? MainRoute.Settings)?.let {
            MainChildDescriptor(
                screen = screen,
                component =
                    DefaultSettingsComponent(
                        componentContext = componentContext,
                        viewModelFactory = viewModelFactory,
                        readingGoalControllerFactory = readingGoalControllerFactory,
                        onDigest = navigator::openDigest,
                    ),
            )
        }
}
