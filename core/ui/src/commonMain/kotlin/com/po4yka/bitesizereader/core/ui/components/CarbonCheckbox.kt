package com.po4yka.bitesizereader.core.ui.components

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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes

@Composable
fun CarbonCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(Dimensions.checkboxCornerRadius)
    val backgroundColor =
        when {
            checked && enabled -> Carbon.theme.interactive
            checked -> Carbon.theme.iconDisabled
            enabled -> Carbon.theme.layer01
            else -> Carbon.theme.layer02
        }
    val borderColor =
        when {
            checked -> backgroundColor
            enabled -> Carbon.theme.borderSubtle00
            else -> Carbon.theme.iconDisabled
        }
    val iconTint =
        if (enabled) {
            Carbon.theme.textOnColor
        } else {
            Carbon.theme.textDisabled
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
                imageVector = CarbonIcons.Checkmark,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(IconSizes.xs),
            )
        }
    }
}
