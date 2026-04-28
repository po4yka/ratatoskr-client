@file:OptIn(com.arkivanov.decompose.DelicateDecomposeApi::class)

package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainNavigator
import com.po4yka.ratatoskr.navigation.MainRouteEntry
import com.po4yka.ratatoskr.navigation.MainTab
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

interface MainComponent {
    val childStack: Value<ChildStack<*, MainChildDescriptor>>

    fun navigateToTab(tab: MainTab)

    fun open(route: AppRoute)
}

internal class DefaultMainComponent(
    componentContext: ComponentContext,
    private val entries: List<MainRouteEntry>,
) : MainComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<AppRoute>()

    private val navigator =
        object : MainNavigator {
            override fun goBack() {
                navigation.pop()
            }

            override fun open(route: AppRoute) {
                logger.info { "Navigate to route: $route" }
                navigation.push(route)
            }
        }

    private val featureRegistry =
        MainFeatureRegistry(
            entries = entries,
            navigator = navigator,
        )

    override val childStack: Value<ChildStack<*, MainChildDescriptor>> =
        childStack(
            source = navigation,
            serializer = AppRoute.serializer(),
            initialConfiguration = featureRegistry.initialRoute(),
            handleBackButton = true,
            childFactory = featureRegistry::createChild,
        )

    override fun open(route: AppRoute) {
        navigator.open(route)
    }

    override fun navigateToTab(tab: MainTab) {
        navigation.bringToFront(featureRegistry.routeForTab(tab))
    }
}
