package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadge
import com.po4yka.ratatoskr.core.ui.components.frost.StatusBadgeSeverity
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.error_view_title
import ratatoskr.core.ui.generated.resources.settings_retry

/** Reusable error-state view. */
@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Alarm severity badge with spark hairline
        StatusBadge(
            label = stringResource(Res.string.error_view_title),
            severity = StatusBadgeSeverity.Alarm,
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        FrostIcon(
            imageVector = AppIcons.WarningAlt,
            contentDescription = null,
            tint = ink.copy(alpha = AppTheme.alpha.secondary),
            modifier = Modifier.size(IconSizes.xl),
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        FrostText(
            text = stringResource(Res.string.error_view_title),
            style = AppTheme.frostType.monoEmph,
            color = ink,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        FrostText(
            text = message,
            style = AppTheme.frostType.monoBody,
            color = ink.copy(alpha = AppTheme.alpha.secondary),
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            BracketButton(
                label = stringResource(Res.string.settings_retry),
                onClick = onRetry,
            )
        }
    }
}
