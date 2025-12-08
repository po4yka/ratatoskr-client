package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.po4yka.bitesizereader.presentation.navigation.DefaultMainComponent
import com.po4yka.bitesizereader.presentation.navigation.MainComponent

@Composable
fun MainScreen(component: MainComponent) {
    val childStack by component.childStack.subscribeAsState()
    val activeChild = childStack.active.instance

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = activeChild is MainComponent.Child.SummaryList,
                    onClick = {
                        if (component is DefaultMainComponent) {
                            component.onTabSelected(DefaultMainComponent.Config.SummaryList)
                        }
                    },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Read Later") }
                )
                NavigationBarItem(
                    selected = activeChild is MainComponent.Child.Collections,
                    onClick = {
                        if (component is DefaultMainComponent) {
                            component.onTabSelected(DefaultMainComponent.Config.Collections)
                        }
                    },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Folder, contentDescription = "Collections") },
                    label = { Text("Collections") }
                )
                NavigationBarItem(
                    selected = activeChild is MainComponent.Child.Settings,
                    onClick = {
                        if (component is DefaultMainComponent) {
                            component.onTabSelected(DefaultMainComponent.Config.Settings)
                        }
                    },
                    icon = { Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Children(
            stack = childStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { child ->
            when (val instance = child.instance) {
                is MainComponent.Child.SummaryList -> SummaryListScreen(component = instance.component)
                is MainComponent.Child.Collections -> CollectionsScreen(onCollectionClick = instance.component::onCollectionClicked)
                is MainComponent.Child.Settings -> SettingsScreen(component = instance.component)
            }
        }
    }
}
