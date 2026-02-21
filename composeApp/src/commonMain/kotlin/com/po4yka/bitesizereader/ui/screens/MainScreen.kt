package com.po4yka.bitesizereader.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.presentation.navigation.MainComponent

/**
 * Main screen with bottom navigation using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun MainScreen(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.subscribeAsState()
    val activeChild = childStack.active.instance

    // Determine if bottom nav should be shown (hide for detail screens)
    val showBottomNav =
        activeChild !is MainComponent.Child.SummaryDetail &&
            activeChild !is MainComponent.Child.CollectionView &&
            activeChild !is MainComponent.Child.SubmitURL

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
    ) {
        // Content area
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            Children(
                stack = childStack,
                modifier = Modifier.fillMaxSize(),
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.SummaryList -> SummaryListScreen(component = instance.component)
                    is MainComponent.Child.SummaryDetail ->
                        SummaryDetailScreen(
                            viewModel = instance.component.viewModel,
                            summaryId = instance.component.summaryId,
                            onBackClick = instance.component::onBackClicked,
                            onShareClick = { },
                        )
                    is MainComponent.Child.Search -> SearchScreen(component = instance.component)
                    is MainComponent.Child.Collections ->
                        CollectionsScreen(component = instance.component)
                    is MainComponent.Child.CollectionView ->
                        CollectionViewScreen(component = instance.component)
                    is MainComponent.Child.Settings -> SettingsScreen(component = instance.component)
                    is MainComponent.Child.SubmitURL ->
                        SubmitURLScreen(component = instance.component)
                }
            }
        }

        // Carbon-style bottom navigation bar (hidden for detail screens)
        if (showBottomNav) {
            BottomNavigation(
                activeChild = activeChild,
                onTabSelected = { tab -> component.navigateToTab(tab) },
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun BottomNavigation(
    activeChild: MainComponent.Child,
    onTabSelected: (MainComponent.Tab) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Carbon.theme.layer01),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItem(
            icon = CarbonIcons.Bookmark,
            label = "Read Later",
            isSelected = activeChild is MainComponent.Child.SummaryList,
            onClick = { onTabSelected(MainComponent.Tab.SUMMARY_LIST) },
        )

        NavItem(
            icon = CarbonIcons.Search,
            label = "Search",
            isSelected = activeChild is MainComponent.Child.Search,
            onClick = { onTabSelected(MainComponent.Tab.SEARCH) },
        )

        NavItem(
            icon = CarbonIcons.Folder,
            label = "Collections",
            isSelected = activeChild is MainComponent.Child.Collections,
            onClick = { onTabSelected(MainComponent.Tab.COLLECTIONS) },
        )

        NavItem(
            icon = CarbonIcons.Settings,
            label = "Settings",
            isSelected = activeChild is MainComponent.Child.Settings,
            onClick = { onTabSelected(MainComponent.Tab.SETTINGS) },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Carbon.theme.iconPrimary else Carbon.theme.iconSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "textColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) Carbon.theme.borderInteractive else Carbon.theme.layer01,
        animationSpec = tween(durationMillis = 200),
        label = "indicatorColor",
    )

    Column(
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top indicator bar for active tab
        Box(
            modifier =
                Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .background(
                        color = indicatorColor,
                        shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp),
                    ),
        )

        Column(
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = Carbon.typography.label01,
                color = textColor,
            )
        }
    }
}
