package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.error_view_title
import ratatoskr.core.ui.generated.resources.settings_retry
import androidx.compose.material3.Button
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

/**
 * Error state view component using Carbon Design System
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = AppIcons.WarningAlt,
            contentDescription = stringResource(Res.string.error_view_title),
            tint = AppTheme.colors.supportError,
            modifier = Modifier.size(IconSizes.xl),
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(Res.string.error_view_title),
            style = AppTheme.type.heading03,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = message,
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))

            Button(onClick = onRetry) {
                Text(stringResource(Res.string.settings_retry))
            }
        }
    }
}
