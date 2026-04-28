package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.Carbon

/**
 * App logo composable: a rounded rectangle in Carbon interactive blue with a white "R" monogram.
 */
@Suppress("FunctionNaming")
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    val interactiveColor = Carbon.theme.interactive
    val fontSize = (size.value * 0.62f).sp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val cornerRadius = this.size.width * 0.20f
            drawRoundRect(
                color = interactiveColor,
                size = this.size,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.25f),
                size = this.size,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(width = this.size.width * 0.025f),
            )
        }
        Text(
            text = "R",
            color = Color.White,
            style =
                TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                ),
        )
    }
}
