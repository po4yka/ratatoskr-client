package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.presentation.navigation.DefaultMainComponent
import com.po4yka.bitesizereader.presentation.navigation.MainComponent

/**
 * Main screen with bottom navigation using Carbon Design System
 */
@Composable
fun MainScreen(component: MainComponent) {
    val childStack by component.childStack.subscribeAsState()
    val activeChild = childStack.active.instance

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Carbon.theme.background)
    ) {
        // Content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Children(
                stack = childStack,
                modifier = Modifier.fillMaxSize()
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.SummaryList -> SummaryListScreen(component = instance.component)
                    is MainComponent.Child.Collections -> CollectionsScreen(
                        onCollectionClick = instance.component::onCollectionClicked
                    )
                    is MainComponent.Child.Settings -> SettingsScreen(component = instance.component)
                }
            }
        }

        // Carbon-style bottom navigation bar
        CarbonBottomNavigation(
            activeChild = activeChild,
            onTabSelected = { config ->
                if (component is DefaultMainComponent) {
                    component.onTabSelected(config)
                }
            }
        )
    }
}

@Composable
private fun CarbonBottomNavigation(
    activeChild: MainComponent.Child,
    onTabSelected: (DefaultMainComponent.Config) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Carbon.theme.layer01),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CarbonNavItem(
            icon = Icons.Default.Home,
            label = "Read Later",
            isSelected = activeChild is MainComponent.Child.SummaryList,
            onClick = { onTabSelected(DefaultMainComponent.Config.SummaryList) }
        )

        CarbonNavItem(
            icon = Icons.Default.Folder,
            label = "Collections",
            isSelected = activeChild is MainComponent.Child.Collections,
            onClick = { onTabSelected(DefaultMainComponent.Config.Collections) }
        )

        CarbonNavItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isSelected = activeChild is MainComponent.Child.Settings,
            onClick = { onTabSelected(DefaultMainComponent.Config.Settings) }
        )
    }
}

@Composable
private fun CarbonNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Carbon.theme.iconPrimary else Carbon.theme.iconSecondary,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            style = Carbon.typography.label01,
            color = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
        )
    }
}
