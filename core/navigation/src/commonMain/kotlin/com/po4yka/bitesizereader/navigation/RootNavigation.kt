package com.po4yka.bitesizereader.navigation

import com.arkivanov.decompose.ComponentContext

enum class RootScreen {
    AUTH,
    MAIN,
}

data class RootChildDescriptor(
    val screen: RootScreen,
    val component: Any,
)

fun interface AuthEntry {
    fun create(
        componentContext: ComponentContext,
        onLoginSuccess: () -> Unit,
    ): RootChildDescriptor
}
