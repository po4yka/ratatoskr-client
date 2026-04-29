package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing

@Composable
fun AppMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return

    val density = LocalDensity.current
    val positionProvider =
        remember(offset, density) {
            AppMenuPositionProvider(
                horizontalOffsetPx = with(density) { offset.x.roundToPx() },
                verticalOffsetPx = with(density) { offset.y.roundToPx() },
            )
        }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties =
            PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = false,
            ),
    ) {
        LayerCard(
            modifier = modifier.widthIn(min = Dimensions.menuWidth),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content,
            )
        }
    }
}

@Composable
fun AppMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = AppTheme.colors.iconSecondary,
    isDestructive: Boolean = false,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val contentColor =
        if (isDestructive) {
            AppTheme.colors.supportError
        } else {
            AppTheme.colors.textPrimary
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint =
                    if (enabled) {
                        if (isDestructive) AppTheme.colors.supportError else leadingIconTint
                    } else {
                        AppTheme.colors.iconDisabled
                    },
                modifier = Modifier.size(IconSizes.xs),
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }

        Text(
            text = label,
            style = AppTheme.type.bodyCompact01,
            color = if (enabled) contentColor else AppTheme.colors.textDisabled,
            modifier = Modifier.weight(1f),
        )

        trailingContent?.invoke(this)
    }
}

private class AppMenuPositionProvider(
    private val horizontalOffsetPx: Int,
    private val verticalOffsetPx: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val preferredX =
            when (layoutDirection) {
                LayoutDirection.Ltr -> anchorBounds.right - popupContentSize.width + horizontalOffsetPx
                LayoutDirection.Rtl -> anchorBounds.left + horizontalOffsetPx
            }
        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val clampedX = preferredX.coerceIn(0, maxX)

        val belowAnchor = anchorBounds.bottom + verticalOffsetPx
        val aboveAnchor = anchorBounds.top - popupContentSize.height - verticalOffsetPx
        val preferredY =
            if (belowAnchor + popupContentSize.height <= windowSize.height) {
                belowAnchor
            } else {
                aboveAnchor
            }
        val maxY = (windowSize.height - popupContentSize.height).coerceAtLeast(0)
        val clampedY = preferredY.coerceIn(0, maxY)

        return IntOffset(clampedX, clampedY)
    }
}

@Composable
fun AppOverflowMenuButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = AppTheme.colors.iconSecondary,
) {
    AppIconButton(
        imageVector = AppIcons.OverflowMenuVertical,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier,
        tint = tint,
        buttonSize = Dimensions.compactIconButtonSize,
        iconSize = IconSizes.sm,
    )
}
