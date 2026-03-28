package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing

@Composable
fun CarbonMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        modifier =
            modifier.border(
                width = Dimensions.borderWidth,
                color = Carbon.theme.borderSubtle00,
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
            ),
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        containerColor = Carbon.theme.layer01,
        tonalElevation = Spacing.xxs,
        shadowElevation = Spacing.xxs,
        content = content,
    )
}

@Composable
fun CarbonMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = Carbon.theme.iconSecondary,
    isDestructive: Boolean = false,
) {
    val contentColor =
        if (isDestructive) {
            Carbon.theme.supportError
        } else {
            Carbon.theme.textPrimary
        }

    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = Carbon.typography.bodyCompact01,
                color = if (enabled) contentColor else Carbon.theme.textDisabled,
            )
        },
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = MenuDefaults.DropdownMenuItemContentPadding,
        leadingIcon =
            leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint =
                            if (enabled) {
                                if (isDestructive) Carbon.theme.supportError else leadingIconTint
                            } else {
                                Carbon.theme.iconDisabled
                            },
                        modifier = Modifier,
                    )
                }
            },
        colors =
            MenuDefaults.itemColors(
                textColor = contentColor,
                leadingIconColor = leadingIconTint,
                disabledTextColor = Carbon.theme.textDisabled,
                disabledLeadingIconColor = Carbon.theme.iconDisabled,
            ),
    )
}

@Composable
fun CarbonOverflowMenuButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Carbon.theme.iconSecondary,
) {
    CarbonIconButton(
        imageVector = CarbonIcons.OverflowMenuVertical,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        tint = tint,
        buttonSize = Dimensions.compactIconButtonSize,
        iconSize = IconSizes.sm,
    )
}
