package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.gabrieldrn.carbon.Carbon
import com.po4yka.ratatoskr.core.ui.theme.Spacing

@Composable
fun CarbonTextButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = Spacing.xs, vertical = Spacing.xxs),
) {
    Text(
        text = label,
        style = Carbon.typography.label01,
        color = if (enabled) Carbon.theme.linkPrimary else Carbon.theme.textDisabled,
        modifier =
            modifier
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(contentPadding),
    )
}
