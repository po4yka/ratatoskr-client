package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.theme.Dimensions

@Composable
fun CarbonLayerCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    backgroundColor: Color = Carbon.theme.layer01,
    borderColor: Color = Carbon.theme.borderSubtle00,
    shape: Shape = RoundedCornerShape(Dimensions.cardCornerRadius),
    content: @Composable BoxScope.() -> Unit,
) {
    val cardModifier =
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(Dimensions.borderWidth, borderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )

    Box(
        modifier = cardModifier,
        content = content,
    )
}
