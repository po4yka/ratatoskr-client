package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.po4yka.ratatoskr.core.ui.components.frost.BracketIconButton
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes

// TODO: callers should migrate to BracketIconButton directly;
//   AppIconButton is a transitional shim
@Composable
fun AppIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = AppTheme.frostColors.ink,
    @Suppress("UNUSED_PARAMETER") buttonSize: Dp = Dimensions.iconButtonSize,
    iconSize: Dp = IconSizes.md,
    @Suppress("UNUSED_PARAMETER") shape: Shape = RectangleShape,
) {
    BracketIconButton(
        onClick = onClick,
        contentDescription = contentDescription,
        enabled = enabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = if (enabled) tint else AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.inactive),
            modifier = Modifier.size(iconSize),
        )
    }
}
