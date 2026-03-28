package com.po4yka.bitesizereader.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext

enum class RootScreen {
    AUTH,
    MAIN,
}

data class RootChildDescriptor(
    val screen: RootScreen,
    val component: Any,
    val render: @Composable () -> Unit,
)

fun interface AuthEntry {
    fun create(
        componentContext: ComponentContext,
        onLoginSuccess: () -> Unit,
    ): RootChildDescriptor
}
