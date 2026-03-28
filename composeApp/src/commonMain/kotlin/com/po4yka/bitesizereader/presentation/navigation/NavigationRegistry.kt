package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.po4yka.bitesizereader.navigation.MainChildDescriptor
import com.po4yka.bitesizereader.navigation.MainNavigator
import com.po4yka.bitesizereader.navigation.MainRoute
import com.po4yka.bitesizereader.navigation.MainRouteEntry
import com.po4yka.bitesizereader.navigation.MainTab

internal class MainFeatureRegistry(
    private val entries: List<MainRouteEntry>,
    private val navigator: MainNavigator,
) {
    private val tabRoutes: Map<MainTab, MainRoute> =
        entries.mapNotNull { entry ->
            val tab = entry.tab ?: return@mapNotNull null
            val defaultRoute =
                requireNotNull(entry.defaultRoute) {
                    "Main route entry $tab must declare a default route"
                }
            tab to defaultRoute
        }.toMap()

    fun createChild(
        route: MainRoute,
        componentContext: ComponentContext,
    ): MainChildDescriptor =
        entries.firstNotNullOfOrNull { entry ->
            entry.create(route, componentContext, navigator)
        } ?: error("No main feature entry registered for route $route")

    fun routeForTab(tab: MainTab): MainRoute =
        tabRoutes[tab] ?: error("No main feature entry registered for tab $tab")
}
