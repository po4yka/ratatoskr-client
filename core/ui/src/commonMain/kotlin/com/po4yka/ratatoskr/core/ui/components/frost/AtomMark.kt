package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Mark style variants for [MarkRange].
 *
 * - [Match] — ink-alpha background only (search result highlight).
 * - [Passage] — ink-alpha background + underline (reader passage annotation).
 */
enum class MarkStyle { Match, Passage }

/**
 * A highlight range within a text string for use with [rememberMarkedAnnotatedString].
 *
 * Implements DESIGN.md § Components — Atom/Mark (v2.13.0, Figma page 12 M23 · Highlights).
 */
@Stable
data class MarkRange(
    val start: Int,
    val endExclusive: Int,
    val style: MarkStyle = MarkStyle.Match,
)

/**
 * Builds and remembers an [AnnotatedString] with Frost mark spans applied to [ranges].
 *
 * [Match] span: ink-alpha 0.08 background.
 * [Passage] span: same background + TextDecoration.Underline.
 */
@Composable
fun rememberMarkedAnnotatedString(
    text: String,
    ranges: List<MarkRange>,
    baseStyle: SpanStyle? = null,
): AnnotatedString {
    val ink = AppTheme.frostColors.ink
    return remember(text, ranges, ink) {
        buildAnnotatedString {
            append(text)
            if (baseStyle != null) addStyle(baseStyle, 0, text.length)
            for (range in ranges) {
                val end = range.endExclusive.coerceAtMost(text.length)
                val start = range.start.coerceAtLeast(0)
                if (start >= end) continue
                val spanStyle =
                    when (range.style) {
                        MarkStyle.Match -> SpanStyle(background = ink.copy(alpha = 0.08f))
                        MarkStyle.Passage ->
                            SpanStyle(
                                background = ink.copy(alpha = 0.08f),
                                textDecoration = TextDecoration.Underline,
                            )
                    }
                addStyle(spanStyle, start, end)
            }
        }
    }
}

@Preview
@Composable
private fun AtomMarkPreview() {
    RatatoskrTheme {
        val annotated =
            rememberMarkedAnnotatedString(
                text = "The quick brown fox jumps over the lazy dog",
                ranges =
                    listOf(
                        MarkRange(4, 9, MarkStyle.Match),
                        MarkRange(10, 15, MarkStyle.Passage),
                    ),
            )
        FrostText(text = annotated)
    }
}
