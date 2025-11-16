package com.po4yka.bitesizereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme
import org.koin.android.ext.android.inject

/**
 * Main activity for Android app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create root navigation component
        val rootComponent = RootComponent(
            componentContext = defaultComponentContext()
        )

        setContent {
            BiteSizeReaderTheme {
                App(rootComponent = rootComponent)
            }
        }
    }
}