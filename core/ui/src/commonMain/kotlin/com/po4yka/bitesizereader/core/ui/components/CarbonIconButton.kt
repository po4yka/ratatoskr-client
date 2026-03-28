package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes

@Composable
fun CarbonIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Carbon.theme.iconPrimary,
    buttonSize: Dp = Dimensions.iconButtonSize,
    iconSize: Dp = IconSizes.md,
    shape: Shape = CircleShape,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .size(buttonSize)
                .clip(shape)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint =
                if (enabled) {
                    tint
                } else {
                    Carbon.theme.iconDisabled
                },
            modifier = Modifier.size(iconSize),
        )
    }
}
