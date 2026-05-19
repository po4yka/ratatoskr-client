package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainNavigator
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import com.po4yka.ratatoskr.navigation.MainTab

internal class MainFeatureRegistry(
    private val entries: List<MainRouteEntry>,
    private val navigator: MainNavigator,
) {
    private val tabRoutes: Map<MainTab, AppRoute> =
        entries.mapNotNull { entry ->
            val tab = entry.tab ?: return@mapNotNull null
            val defaultRoute =
                requireNotNull(entry.defaultRoute) {
                    "Main route entry $tab must declare a default route"
                }
            tab to defaultRoute
        }.toMap()

    fun createChild(
        route: AppRoute,
        componentContext: ComponentContext,
    ): MainChildDescriptor =
        entries.firstNotNullOfOrNull { entry ->
            entry.create(route, componentContext, navigator)
        } ?: error("No main feature entry registered for route $route")

    fun routeForTab(tab: MainTab): AppRoute = tabRoutes[tab] ?: error("No main feature entry registered for tab $tab")

    fun initialRoute(): AppRoute =
        MainTab.entries.firstNotNullOfOrNull { tabRoutes[it] }
            ?: error("No tab-backed route entries registered")
}
