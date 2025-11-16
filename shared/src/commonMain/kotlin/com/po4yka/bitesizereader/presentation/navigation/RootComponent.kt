package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value

/**
 * Root navigation component for the application
 * Simplified for use with Android ViewModels and Koin
 */
class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Screen>()

    val stack: Value<ChildStack<*, Screen>> =
        childStack(
            source = navigation,
            serializer = Screen.serializer(),
            initialConfiguration = Screen.Auth,
            handleBackButton = true,
            childFactory = { config, _ -> config }
        )

    fun navigateToSummaryList() {
        navigation.push(Screen.SummaryList)
    }

    fun navigateToSummaryDetail(id: Int) {
        navigation.push(Screen.SummaryDetail(id))
    }

    fun navigateToSubmitUrl() {
        navigation.push(Screen.SubmitUrl)
    }

    fun navigateToSearch() {
        navigation.push(Screen.Search)
    }

    fun pop() {
        navigation.pop()
    }
}
