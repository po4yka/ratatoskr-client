package com.po4yka.bitesizereader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import platform.UIKit.UIViewController

fun MainViewController(rootComponent: RootComponent): UIViewController =
    ComposeUIViewController {
        App(
            rootComponent = rootComponent,
            modifier = Modifier.fillMaxSize(),
        )
    }
