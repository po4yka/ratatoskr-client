package com.po4yka.bitesizereader.core.ui.components

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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.Spacing

@Composable
fun CarbonSelectableChip(
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
            Carbon.theme.backgroundInverse
        } else {
            Carbon.theme.layer01
        }
    val borderColor =
        if (selected) {
            Carbon.theme.backgroundInverse
        } else {
            Carbon.theme.borderSubtle00
        }
    val textColor =
        if (selected) {
            Carbon.theme.textOnColor
        } else {
            Carbon.theme.textSecondary
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
            style = Carbon.typography.bodyCompact01,
            color = textColor,
        )
    }
}
