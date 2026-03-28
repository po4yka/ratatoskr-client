package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.navigation.RootChildDescriptor
import com.po4yka.bitesizereader.navigation.RootScreen
import com.po4yka.bitesizereader.presentation.navigation.AuthComponent
import com.po4yka.bitesizereader.presentation.navigation.MainComponent
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.components.LocalImageUrlTransformer
import com.po4yka.bitesizereader.ui.screens.AuthScreen
import com.po4yka.bitesizereader.ui.screens.MainScreen
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme

/** Main app composable with Decompose navigation */
@Composable
fun App(
    rootComponent: RootComponent,
    imageUrlTransformer: (String) -> String,
    modifier: Modifier = Modifier,
) {
    val childStack = rootComponent.childStack.subscribeAsState()

    BiteSizeReaderTheme {
        CompositionLocalProvider(
            LocalImageUrlTransformer provides imageUrlTransformer,
        ) {
            Children(
                stack = childStack.value,
                modifier =
                    modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
            ) { child ->
                val instance = child.instance
                when (instance.screen) {
                    RootScreen.AUTH ->
                        AuthScreen(
                            component = instance.requireComponent<AuthComponent>(),
                        )
                    RootScreen.MAIN ->
                        MainScreen(component = instance.requireComponent<MainComponent>())
                }
            }
        }
    }
}

private inline fun <reified T> RootChildDescriptor.requireComponent(): T =
    component as? T ?: error("Expected ${T::class.simpleName} for $screen, got ${component::class.simpleName}")
