package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon

/**
 * App logo composable: a rounded rectangle in Carbon interactive blue with a stylised open-book glyph.
 */
@Suppress("FunctionNaming")
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
) {
    val interactiveColor = Carbon.theme.interactive

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size
        val cornerRadius = canvasSize.width * 0.20f

        // Background rounded rectangle
        drawRoundRect(
            color = interactiveColor,
            size = canvasSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
        )

        // White border (subtle inset)
        val strokeWidth = canvasSize.width * 0.025f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.25f),
            size = canvasSize,
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = strokeWidth),
        )

        // Book icon: two page rectangles + spine line
        val bookPadding = canvasSize.width * 0.18f
        val bookTop = canvasSize.height * 0.22f
        val bookBottom = canvasSize.height * 0.76f
        val bookLeft = canvasSize.width * 0.15f
        val bookRight = canvasSize.width * 0.85f
        val spineX = canvasSize.width * 0.50f
        val bookHeight = bookBottom - bookTop
        val halfWidth = (spineX - bookLeft)
        val pageStroke = canvasSize.width * 0.04f
        val pageCorner = canvasSize.width * 0.05f

        // Left page
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(bookLeft, bookTop),
            size = Size(halfWidth - pageStroke * 0.5f, bookHeight),
            cornerRadius = CornerRadius(pageCorner, pageCorner),
            style = Stroke(width = pageStroke),
        )

        // Right page
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(spineX + pageStroke * 0.5f, bookTop),
            size = Size(halfWidth - pageStroke * 0.5f, bookHeight),
            cornerRadius = CornerRadius(pageCorner, pageCorner),
            style = Stroke(width = pageStroke),
        )

        // Spine / center line (solid)
        drawLine(
            color = Color.White,
            start = Offset(spineX, bookTop + pageCorner),
            end = Offset(spineX, bookBottom - pageCorner),
            strokeWidth = pageStroke,
            cap = StrokeCap.Round,
        )

        // Three horizontal lines on left page (article lines)
        val lineColor = Color.White.copy(alpha = 0.7f)
        val lineStroke = canvasSize.width * 0.025f
        val lineStartX = bookLeft + bookPadding * 0.5f
        val lineEndX = spineX - bookPadding * 0.6f
        val lineSpacing = bookHeight / 4f
        for (i in 1..3) {
            val y = bookTop + lineSpacing * i
            drawLine(
                color = lineColor,
                start = Offset(lineStartX, y),
                end = Offset(lineEndX, y),
                strokeWidth = lineStroke,
                cap = StrokeCap.Round,
            )
        }

        // Three lines on right page
        val rLineStartX = spineX + bookPadding * 0.6f
        val rLineEndX = bookRight - bookPadding * 0.5f
        for (i in 1..3) {
            val y = bookTop + lineSpacing * i
            drawLine(
                color = lineColor,
                start = Offset(rLineStartX, y),
                end = Offset(rLineEndX, y),
                strokeWidth = lineStroke,
                cap = StrokeCap.Round,
            )
        }
    }
}
