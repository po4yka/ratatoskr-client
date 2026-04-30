package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * App logo composable: a rectangle in Frost ink with a contrasting page "R" monogram.
 */
@Suppress("FunctionNaming")
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    val inkColor = AppTheme.frostColors.ink
    val pageColor = AppTheme.frostColors.page
    val fontSize = (size.value * 0.62f).sp

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Frost: rectangle (0dp radius), ink fill
            drawRect(color = inkColor)
            // Hairline inner border in page color for definition
            drawRect(
                color = pageColor.copy(alpha = 0.25f),
                style = Stroke(width = this.size.width * 0.025f),
            )
        }
        FrostText(
            text = "R",
            color = pageColor,
            style =
                TextStyle(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                ),
        )
    }
}
