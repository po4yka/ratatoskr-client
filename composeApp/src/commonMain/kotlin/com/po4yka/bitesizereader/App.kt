package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.screens.AuthScreen
import com.po4yka.bitesizereader.ui.screens.MainScreen

/** Main app composable with Decompose navigation */
@Composable
fun App(
    rootComponent: RootComponent,
    modifier: Modifier = Modifier,
) {
    val childStack = rootComponent.childStack.subscribeAsState()

    Children(
        stack = childStack.value,
        modifier =
            modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Auth ->
                AuthScreen(
                    component = instance.component,
                )

            is RootComponent.Child.Main -> MainScreen(component = instance.component)
        }
    }
}
