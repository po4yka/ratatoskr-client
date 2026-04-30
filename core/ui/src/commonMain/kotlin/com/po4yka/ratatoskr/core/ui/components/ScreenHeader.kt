package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.a11y_navigate_back

/**
 * Standardized screen header component for consistent header styling across the app.
 *
 * @param title The title text to display in the header
 * @param modifier Modifier for the header row
 * @param isDetailScreen If true, uses smaller height (56dp) for detail screens with back button.
 *                       If false, uses larger height (64dp) for main navigation screens.
 * @param onBackClick Optional callback for back navigation. If provided, shows a back arrow button.
 * @param actions Optional composable lambda for action buttons on the right side of the header.
 */
@Suppress("FunctionNaming")
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    isDetailScreen: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    val headerHeight =
        if (isDetailScreen) {
            Dimensions.detailHeaderHeight
        } else {
            Dimensions.headerHeight
        }

    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .padding(horizontal = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button (optional)
            if (onBackClick != null) {
                AppIconButton(
                    imageVector = AppIcons.ArrowLeft,
                    contentDescription = stringResource(Res.string.a11y_navigate_back),
                    onClick = onBackClick,
                )
            }

            // Title — mono uppercase per Frost editorial spec
            FrostText(
                text = title.uppercase(),
                style = AppTheme.frostType.monoEmph,
                color = AppTheme.frostColors.ink,
                modifier = Modifier.weight(1f).semantics { heading() },
            )

            // Action buttons (optional)
            actions?.invoke(this)
        }
        // Hairline bottom border
        FrostDivider(alpha = AppTheme.border.separatorAlpha)
    }
}

/**
 * Standardized header icon button for consistent icon styling in headers.
 *
 * @param icon The icon to display
 * @param contentDescription Accessibility description for the icon
 * @param onClick Callback when the button is clicked
 * @param modifier Modifier for the button container
 */
@Suppress("FunctionNaming")
@Composable
fun HeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: androidx.compose.ui.graphics.Color = AppTheme.frostColors.ink,
) {
    AppIconButton(
        imageVector = icon,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        tint = tint,
        iconSize = IconSizes.md,
    )
}
