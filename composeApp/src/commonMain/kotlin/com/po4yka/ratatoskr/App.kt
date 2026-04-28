package com.po4yka.ratatoskr

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.po4yka.ratatoskr.presentation.navigation.RootComponent
import com.po4yka.ratatoskr.core.ui.components.LocalImageUrlTransformer
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/** Main app composable with Decompose navigation */
@Composable
fun App(
    rootComponent: RootComponent,
    imageUrlTransformer: (String) -> String,
    modifier: Modifier = Modifier,
) {
    RatatoskrTheme {
        CompositionLocalProvider(
            LocalImageUrlTransformer provides imageUrlTransformer,
        ) {
            Children(
                stack = rootComponent.childStack,
                modifier =
                    modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
            ) { child -> child.instance.render() }
        }
    }
}
