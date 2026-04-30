@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost-native text composable backed by [BasicText] (no Material 3 dependency).
 *
 * Defaults to [AppTheme.frostType.monoBody] style and [AppTheme.frostColors.ink] color.
 * Implements DESIGN.md § Typography — JetBrains Mono as universal UI type face.
 */
@Composable
fun FrostText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val baseStyle = style ?: AppTheme.frostType.monoBody
    val resolvedColor = if (color == Color.Unspecified) AppTheme.frostColors.ink else color
    BasicText(
        text = text,
        modifier = modifier,
        style = baseStyle.copy(color = resolvedColor),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/**
 * Frost-native text composable for [AnnotatedString] backed by [BasicText].
 *
 * Same defaults as the [String] overload. Implements DESIGN.md § Typography.
 */
@Composable
fun FrostText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val baseStyle = style ?: AppTheme.frostType.monoBody
    val resolvedColor = if (color == Color.Unspecified) AppTheme.frostColors.ink else color
    BasicText(
        text = text,
        modifier = modifier,
        style = baseStyle.copy(color = resolvedColor),
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Preview
@Composable
private fun FrostTextPreview() {
    RatatoskrTheme {
        FrostText(text = "Hello, Frost")
    }
}
