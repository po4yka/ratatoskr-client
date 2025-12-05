package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.po4yka.bitesizereader.ui.screens.AuthScreen
import com.po4yka.bitesizereader.ui.screens.SummaryListScreen

/** Main app composable with Decompose navigation */
@Composable
fun App(
    rootComponent: RootComponent,
    modifier: Modifier = Modifier,
    onLoginClick: (AuthViewModel) -> Unit = {},
) {
    val childStack = rootComponent.childStack.subscribeAsState()

    Children(
        stack = childStack.value,
        modifier = modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Auth -> AuthScreen(
                component = instance.component,
                onLoginClick = onLoginClick,
            )

            is RootComponent.Child.Main -> SummaryListScreen(component = instance.component.summaryListComponent)
        }
    }
}
