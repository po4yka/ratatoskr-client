package com.po4yka.bitesizereader.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.nav_collections
import bitesizereader.composeapp.generated.resources.nav_read_later
import bitesizereader.composeapp.generated.resources.nav_search
import bitesizereader.composeapp.generated.resources.nav_settings
import bitesizereader.composeapp.generated.resources.nav_stats
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.presentation.navigation.MainComponent
import com.po4yka.bitesizereader.ui.components.LocalReadingGoalViewModel

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

    // Determine if navigation should be shown (hide for detail screens)
    val showNav =
        activeChild !is MainComponent.Child.SummaryDetail &&
            activeChild !is MainComponent.Child.CollectionView &&
            activeChild !is MainComponent.Child.SubmitURL &&
            activeChild !is MainComponent.Child.Digest &&
            activeChild !is MainComponent.Child.CustomDigestCreate &&
            activeChild !is MainComponent.Child.CustomDigestView

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(Carbon.theme.background),
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
                    ScreenContent(
                        childStack = childStack,
                        readingGoalViewModel = component.readingGoalViewModel,
                    )
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
                    ScreenContent(
                        childStack = childStack,
                        readingGoalViewModel = component.readingGoalViewModel,
                    )
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
private fun ScreenContent(
    childStack: ChildStack<*, MainComponent.Child>,
    readingGoalViewModel: com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel,
) {
    CompositionLocalProvider(
        LocalReadingGoalViewModel provides readingGoalViewModel,
    ) {
        Children(
            stack = childStack,
            modifier = Modifier.fillMaxSize(),
            animation = stackAnimation(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is MainComponent.Child.SummaryList -> SummaryListScreen(component = instance.component)
                is MainComponent.Child.SummaryDetail ->
                    SummaryDetailScreen(component = instance.component)
                is MainComponent.Child.Search -> SearchScreen(component = instance.component)
                is MainComponent.Child.Collections ->
                    CollectionsScreen(component = instance.component)
                is MainComponent.Child.CollectionView ->
                    CollectionViewScreen(component = instance.component)
                is MainComponent.Child.Stats -> StatsScreen(component = instance.component)
                is MainComponent.Child.Settings -> SettingsScreen(component = instance.component)
                is MainComponent.Child.SubmitURL ->
                    SubmitURLScreen(component = instance.component)
                is MainComponent.Child.Digest ->
                    DigestScreen(component = instance.component)
                is MainComponent.Child.CustomDigestCreate ->
                    CustomDigestCreateScreen(component = instance.component)
                is MainComponent.Child.CustomDigestView ->
                    CustomDigestViewScreen(component = instance.component)
            }
        }
    }
}

/** Vertical navigation rail shown on large screens. */
@Suppress("FunctionNaming")
@Composable
private fun NavigationRail(
    activeChild: MainComponent.Child,
    onTabSelected: (MainComponent.Tab) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(80.dp)
                .fillMaxHeight()
                .background(Carbon.theme.layer01),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        RailItem(
            icon = CarbonIcons.Bookmark,
            label = stringResource(Res.string.nav_read_later),
            isSelected = activeChild is MainComponent.Child.SummaryList,
            onClick = { onTabSelected(MainComponent.Tab.SUMMARY_LIST) },
        )

        RailItem(
            icon = CarbonIcons.Search,
            label = stringResource(Res.string.nav_search),
            isSelected = activeChild is MainComponent.Child.Search,
            onClick = { onTabSelected(MainComponent.Tab.SEARCH) },
        )

        RailItem(
            icon = CarbonIcons.Folder,
            label = stringResource(Res.string.nav_collections),
            isSelected = activeChild is MainComponent.Child.Collections,
            onClick = { onTabSelected(MainComponent.Tab.COLLECTIONS) },
        )

        RailItem(
            icon = CarbonIcons.Document,
            label = stringResource(Res.string.nav_stats),
            isSelected = activeChild is MainComponent.Child.Stats,
            onClick = { onTabSelected(MainComponent.Tab.STATS) },
        )

        RailItem(
            icon = CarbonIcons.Settings,
            label = stringResource(Res.string.nav_settings),
            isSelected = activeChild is MainComponent.Child.Settings,
            onClick = { onTabSelected(MainComponent.Tab.SETTINGS) },
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
        targetValue = if (isSelected) Carbon.theme.iconPrimary else Carbon.theme.iconSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "railIconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Carbon.theme.textPrimary else Carbon.theme.textSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "railTextColor",
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) Carbon.theme.borderInteractive else Carbon.theme.layer01,
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
                        shape = RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp),
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
                style = Carbon.typography.label01,
                color = textColor,
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
            label = stringResource(Res.string.nav_read_later),
            isSelected = activeChild is MainComponent.Child.SummaryList,
            onClick = { onTabSelected(MainComponent.Tab.SUMMARY_LIST) },
        )

        NavItem(
            icon = CarbonIcons.Search,
            label = stringResource(Res.string.nav_search),
            isSelected = activeChild is MainComponent.Child.Search,
            onClick = { onTabSelected(MainComponent.Tab.SEARCH) },
        )

        NavItem(
            icon = CarbonIcons.Folder,
            label = stringResource(Res.string.nav_collections),
            isSelected = activeChild is MainComponent.Child.Collections,
            onClick = { onTabSelected(MainComponent.Tab.COLLECTIONS) },
        )

        NavItem(
            icon = CarbonIcons.Document,
            label = stringResource(Res.string.nav_stats),
            isSelected = activeChild is MainComponent.Child.Stats,
            onClick = { onTabSelected(MainComponent.Tab.STATS) },
        )

        NavItem(
            icon = CarbonIcons.Settings,
            label = stringResource(Res.string.nav_settings),
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
