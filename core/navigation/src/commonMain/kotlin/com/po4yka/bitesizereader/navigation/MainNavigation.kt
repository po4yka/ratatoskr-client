package com.po4yka.bitesizereader.navigation

import com.arkivanov.decompose.ComponentContext
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

enum class MainTab {
    SUMMARY_LIST,
    SEARCH,
    COLLECTIONS,
    STATS,
    SETTINGS,
}

@Serializable
data class AppRoute(
    val featureId: String,
    val screenId: String,
    val argument: String? = null,
)

data class MainChildDescriptor(
    val route: AppRoute,
    val tab: MainTab?,
    val component: Any,
    val render: @Composable () -> Unit,
)

interface MainNavigator {
    fun goBack()

    fun open(route: AppRoute)
}

interface MainRouteEntry {
    val tab: MainTab?
    val defaultRoute: AppRoute?

    fun create(
        route: AppRoute,
        componentContext: ComponentContext,
        navigator: MainNavigator,
    ): MainChildDescriptor?
}
