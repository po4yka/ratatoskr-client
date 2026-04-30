package com.po4yka.ratatoskr.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.nav_collections
import ratatoskr.core.ui.generated.resources.nav_read_later
import ratatoskr.core.ui.generated.resources.nav_search
import ratatoskr.core.ui.generated.resources.nav_settings
import ratatoskr.core.ui.generated.resources.nav_stats
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainTab
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.presentation.navigation.MainComponent

/** Minimum width to switch from bottom-bar to side navigation rail layout. */
private val EXPANDED_WIDTH_THRESHOLD = 600.dp

/**
 * Main screen with adaptive navigation:
 * - Compact width (< 600 dp, phone): bottom navigation bar
 * - Expanded width (>= 600 dp, tablet / iPad / Desktop): left navigation rail
 */
@Suppress("FunctionNaming")
@Composable
fun MainScreen(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.subscribeAsState()
    val activeChild = childStack.active.instance

    val showNav = activeChild.tab != null

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(AppTheme.colors.background),
    ) {
        val isExpanded = maxWidth >= EXPANDED_WIDTH_THRESHOLD

        if (isExpanded) {
            // Tablet / iPad / Desktop: side navigation rail + content
            Row(modifier = Modifier.fillMaxSize()) {
                if (showNav) {
                    NavigationRail(
                        activeChild = activeChild,
                        onTabSelected = { tab -> component.navigateToTab(tab) },
                    )
                }

                // Content area
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ScreenContent(childStack = childStack)
                }
            }
        } else {
            // Phone: content above, bottom navigation below
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                ) {
                    ScreenContent(childStack = childStack)
                }

                if (showNav) {
                    BottomNavigation(
                        activeChild = activeChild,
                        onTabSelected = { tab -> component.navigateToTab(tab) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(childStack: ChildStack<*, MainChildDescriptor>) {
    Children(
        stack = childStack,
        modifier = Modifier.fillMaxSize(),
        animation = stackAnimation(fade()),
    ) { child -> child.instance.render() }
}

/** Vertical navigation rail shown on large screens. */
@Suppress("FunctionNaming")
@Composable
private fun NavigationRail(
    activeChild: MainChildDescriptor,
    onTabSelected: (MainTab) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(AppTheme.colors.layer01),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        RailItem(
            icon = AppIcons.Bookmark,
            label = stringResource(Res.string.nav_read_later),
            isSelected = activeChild.tab == MainTab.SUMMARY_LIST,
            onClick = { onTabSelected(MainTab.SUMMARY_LIST) },
        )

        RailItem(
            icon = AppIcons.Search,
            label = stringResource(Res.string.nav_search),
            isSelected = activeChild.tab == MainTab.SEARCH,
            onClick = { onTabSelected(MainTab.SEARCH) },
        )

        RailItem(
            icon = AppIcons.Folder,
            label = stringResource(Res.string.nav_collections),
            isSelected = activeChild.tab == MainTab.COLLECTIONS,
            onClick = { onTabSelected(MainTab.COLLECTIONS) },
        )

        RailItem(
            icon = AppIcons.Document,
            label = stringResource(Res.string.nav_stats),
            isSelected = activeChild.tab == MainTab.STATS,
            onClick = { onTabSelected(MainTab.STATS) },
        )

        RailItem(
            icon = AppIcons.Settings,
            label = stringResource(Res.string.nav_settings),
            isSelected = activeChild.tab == MainTab.SETTINGS,
            onClick = { onTabSelected(MainTab.SETTINGS) },
        )
    }
}

@Suppress("FunctionNaming")
@Composable
private fun RailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) AppTheme.colors.iconPrimary else AppTheme.colors.iconSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "railIconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "railTextColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) AppTheme.colors.borderInteractive else AppTheme.colors.layer01,
        animationSpec = tween(durationMillis = 200),
        label = "railIndicatorColor",
    )

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left active indicator bar
        Box(
            modifier =
                Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .background(
                        color = indicatorColor,
                        shape = RectangleShape,
                    ),
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
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
                style = AppTheme.type.label01,
                color = textColor,
            )
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun BottomNavigation(
    activeChild: MainChildDescriptor,
    onTabSelected: (MainTab) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(AppTheme.colors.layer01),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItem(
            icon = AppIcons.Bookmark,
            label = stringResource(Res.string.nav_read_later),
            isSelected = activeChild.tab == MainTab.SUMMARY_LIST,
            onClick = { onTabSelected(MainTab.SUMMARY_LIST) },
        )

        NavItem(
            icon = AppIcons.Search,
            label = stringResource(Res.string.nav_search),
            isSelected = activeChild.tab == MainTab.SEARCH,
            onClick = { onTabSelected(MainTab.SEARCH) },
        )

        NavItem(
            icon = AppIcons.Folder,
            label = stringResource(Res.string.nav_collections),
            isSelected = activeChild.tab == MainTab.COLLECTIONS,
            onClick = { onTabSelected(MainTab.COLLECTIONS) },
        )

        NavItem(
            icon = AppIcons.Document,
            label = stringResource(Res.string.nav_stats),
            isSelected = activeChild.tab == MainTab.STATS,
            onClick = { onTabSelected(MainTab.STATS) },
        )

        NavItem(
            icon = AppIcons.Settings,
            label = stringResource(Res.string.nav_settings),
            isSelected = activeChild.tab == MainTab.SETTINGS,
            onClick = { onTabSelected(MainTab.SETTINGS) },
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
        targetValue = if (isSelected) AppTheme.colors.iconPrimary else AppTheme.colors.iconSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "textColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) AppTheme.colors.borderInteractive else AppTheme.colors.layer01,
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
                        shape = RectangleShape,
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
                style = AppTheme.type.label01,
                color = textColor,
            )
        }
    }
}
