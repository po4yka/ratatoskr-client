package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType

/**
 * Empty state view component using Carbon Design System
 */
@Suppress("FunctionNaming", "LongParameterList") // Composable naming convention; UI component with optional params
@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector = CarbonIcons.Document,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Carbon.theme.textSecondary,
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = Carbon.typography.heading03,
            color = Carbon.theme.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
            textAlign = TextAlign.Center,
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                label = actionText,
                onClick = onAction,
                buttonType = ButtonType.Primary,
            )
        }
    }
}
