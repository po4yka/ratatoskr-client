package com.po4yka.ratatoskr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.ratatoskr.presentation.navigation.RootComponent
import platform.UIKit.UIViewController

fun MainViewController(rootComponent: RootComponent): UIViewController =
    ComposeUIViewController {
        val compositionRoot = iosCompositionRoot()
        App(
            rootComponent = rootComponent,
            imageUrlTransformer = compositionRoot.imageUrlTransformer(),
            modifier = Modifier.fillMaxSize(),
        )
    }
