package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes

@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(Dimensions.checkboxCornerRadius)
    val backgroundColor =
        when {
            checked && enabled -> AppTheme.colors.interactive
            checked -> AppTheme.colors.iconDisabled
            enabled -> AppTheme.colors.layer01
            else -> AppTheme.colors.layer02
        }
    val borderColor =
        when {
            checked -> backgroundColor
            enabled -> AppTheme.colors.borderSubtle00
            else -> AppTheme.colors.iconDisabled
        }
    val iconTint =
        if (enabled) {
            AppTheme.colors.textOnColor
        } else {
            AppTheme.colors.textDisabled
        }

    Box(
        modifier =
            modifier
                .size(Dimensions.checkboxSize)
                .clip(shape)
                .background(backgroundColor)
                .border(Dimensions.borderWidth, borderColor, shape)
                .toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = AppIcons.Checkmark,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(IconSizes.xs),
            )
        }
    }
}
