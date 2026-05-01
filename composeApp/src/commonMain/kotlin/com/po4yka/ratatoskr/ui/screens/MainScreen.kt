package com.po4yka.ratatoskr.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.selection.selectableGroup
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.navigation.MainChildDescriptor
import com.po4yka.ratatoskr.navigation.MainTab
import com.po4yka.ratatoskr.presentation.navigation.MainComponent
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.nav_collections
import ratatoskr.core.ui.generated.resources.nav_read_later
import ratatoskr.core.ui.generated.resources.nav_search
import ratatoskr.core.ui.generated.resources.nav_settings
import ratatoskr.core.ui.generated.resources.nav_stats

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

/** Vertical navigation rail shown on large screens (96 dp wide, Frost-styled). */
@Suppress("FunctionNaming")
@Composable
private fun NavigationRail(
    activeChild: MainChildDescriptor,
    onTabSelected: (MainTab) -> Unit,
) {
    val ink = AppTheme.frostColors.ink
    Column(
        modifier =
            Modifier
                .width(96.dp)
                .fillMaxHeight()
                .background(AppTheme.frostColors.page)
                .drawBehind {
                    // Trailing hairline border (right edge, facing content)
                    drawLine(
                        color = ink,
                        start = Offset(size.width, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .selectableGroup(),
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
    val ink = AppTheme.frostColors.ink
    val motionSpec = AppTheme.motion.toast

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) AppTheme.alpha.active else AppTheme.alpha.inactive,
        animationSpec = motionSpec,
        label = "railAlpha",
    )

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(64.dp)
                .drawBehind {
                    // 4 dp leading ink hairline for selected item
                    if (isSelected) {
                        drawRect(
                            color = ink,
                            topLeft = Offset(0f, 0f),
                            size = Size(4.dp.toPx(), size.height),
                        )
                    }
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = FrostIndication,
                    onClick = onClick,
                )
                .semantics {
                    role = Role.Tab
                    selected = isSelected
                },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Leading indicator width placeholder so content stays centered
        Spacer(modifier = Modifier.width(4.dp))

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FrostIcon(
                imageVector = icon,
                contentDescription = label,
                tint = ink.copy(alpha = alpha),
                modifier = Modifier.size(24.dp),
            )
            FrostText(
                text = label.uppercase(),
                style = AppTheme.frostType.monoXs,
                color = ink.copy(alpha = alpha),
                maxLines = 1,
            )
        }
    }
}

/** Bottom navigation bar shown on compact screens (Frost-styled, 56 dp tall). */
@Suppress("FunctionNaming")
@Composable
private fun BottomNavigation(
    activeChild: MainChildDescriptor,
    onTabSelected: (MainTab) -> Unit,
) {
    val ink = AppTheme.frostColors.ink
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(AppTheme.frostColors.page)
                .drawBehind {
                    // Top hairline border separating nav from content
                    drawLine(
                        color = ink,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .selectableGroup(),
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
    val ink = AppTheme.frostColors.ink
    val motionSpec = AppTheme.motion.toast

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) AppTheme.alpha.active else AppTheme.alpha.inactive,
        animationSpec = motionSpec,
        label = "navAlpha",
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier =
            Modifier
                .height(56.dp)
                .padding(horizontal = 12.dp)
                .drawBehind {
                    // 4 dp ink hairline at the top edge for the selected item
                    if (isSelected) {
                        drawRect(
                            color = ink,
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, 4.dp.toPx()),
                        )
                    }
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = FrostIndication,
                    onClick = onClick,
                )
                .semantics {
                    role = Role.Tab
                    selected = isSelected
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FrostIcon(
            imageVector = icon,
            contentDescription = label,
            tint = ink.copy(alpha = alpha),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        FrostText(
            text = label.uppercase(),
            style = AppTheme.frostType.monoXs,
            color = ink.copy(alpha = alpha),
            maxLines = 1,
        )
    }
}
