package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToSubmitUrl(prefilledUrl: String)

    fun navigateToSummaryDetail(summaryId: String)

    sealed class Child {
        class Auth(val component: AuthComponent) : Child()

        class Main(val component: MainComponent) : Child()
    }
}
