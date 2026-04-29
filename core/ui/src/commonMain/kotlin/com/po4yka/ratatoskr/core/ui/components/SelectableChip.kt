package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role = Role.RadioButton,
    contentPadding: PaddingValues = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
) {
    val shape = RoundedCornerShape(Dimensions.chipCornerRadius)
    val backgroundColor =
        if (selected) {
            AppTheme.colors.backgroundInverse
        } else {
            AppTheme.colors.layer01
        }
    val borderColor =
        if (selected) {
            AppTheme.colors.backgroundInverse
        } else {
            AppTheme.colors.borderSubtle00
        }
    val textColor =
        if (selected) {
            AppTheme.colors.textOnColor
        } else {
            AppTheme.colors.textSecondary
        }

    Box(
        modifier =
            modifier
                .clip(shape)
                .background(backgroundColor)
                .border(Dimensions.borderWidth, borderColor, shape)
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    role = role,
                    onClick = onClick,
                )
                .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.type.bodyCompact01,
            color = textColor,
        )
    }
}
