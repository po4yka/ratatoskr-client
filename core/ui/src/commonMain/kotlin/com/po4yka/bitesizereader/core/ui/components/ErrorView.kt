package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.error_view_title
import bitesizereader.core.ui.generated.resources.settings_retry
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
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
            imageVector = CarbonIcons.WarningAlt,
            contentDescription = stringResource(Res.string.error_view_title),
            tint = Carbon.theme.supportError,
            modifier = Modifier.size(IconSizes.xl),
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(Res.string.error_view_title),
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = message,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
            textAlign = TextAlign.Center,
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Spacing.lg))

            Button(
                label = stringResource(Res.string.settings_retry),
                onClick = onRetry,
                buttonType = ButtonType.Primary,
            )
        }
    }
}
