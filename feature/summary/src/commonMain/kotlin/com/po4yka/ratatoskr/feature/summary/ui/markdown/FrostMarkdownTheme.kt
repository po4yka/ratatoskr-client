package com.po4yka.ratatoskr.feature.summary.ui.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.utils.getUnescapedTextInNode
import com.po4yka.ratatoskr.core.ui.components.frost.PullQuote
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * Frost-aligned markdown colors — ink on page; spark only for error fallback inside the reader.
 *
 * Two-color rule: text=ink, code backgrounds=ink-alpha 0.08, divider=ink-alpha 0.40.
 * No Material 3 dependency.
 */
@Composable
fun rememberFrostMarkdownColors(): MarkdownColors {
    val ink = AppTheme.frostColors.ink
    val divider = ink.copy(alpha = AppTheme.border.separatorAlpha)
    return remember(ink) {
        DefaultMarkdownColors(
            text = ink,
            codeBackground = ink.copy(alpha = 0.08f),
            inlineCodeBackground = ink.copy(alpha = 0.08f),
            dividerColor = divider,
            tableBackground = ink.copy(alpha = 0.04f),
        )
    }
}

/**
 * Frost markdown typography: paragraph and text render in serifReader (Source Serif 4 italic),
 * per DESIGN.md reader-mode anchor. h1..h6 render in monoEmph (ExtraBold) at descending sizes.
 * code/inlineCode render in monoBody. list/bullet/ordered/link render in monoBody.
 *
 * @param scale font size multiplier from reading preferences (default 1f)
 * @param lineScale line height multiplier from reading preferences (default 1f)
 */
@Composable
fun rememberFrostMarkdownTypography(
    scale: Float = 1f,
    lineScale: Float = 1f,
): MarkdownTypography {
    val frostType = AppTheme.frostType
    val ink = AppTheme.frostColors.ink

    return remember(frostType, scale, lineScale, ink) {
        fun TextStyle.scaled(): TextStyle =
            copy(
                fontSize = fontSize * scale,
                lineHeight = if (lineHeight.isSp) lineHeight * lineScale else lineHeight,
            )

        // h1..h6: monoEmph at descending multipliers relative to monoEmph base (13sp)
        val h1 = frostType.monoEmph.copy(fontSize = frostType.monoEmph.fontSize * 1.85f).scaled()
        val h2 = frostType.monoEmph.copy(fontSize = frostType.monoEmph.fontSize * 1.54f).scaled()
        val h3 = frostType.monoEmph.copy(fontSize = frostType.monoEmph.fontSize * 1.30f).scaled()
        val h4 =
            frostType.monoEmph.copy(
                fontSize = frostType.monoEmph.fontSize * 1.15f,
                fontWeight = FontWeight.Bold,
            ).scaled()
        val h5 = frostType.monoEmph.scaled()
        val h6 = frostType.monoXs.copy(fontWeight = FontWeight.Bold).scaled()

        // paragraph and text: serifReader (Source Serif 4 italic) per DESIGN.md reader-mode anchor
        val body = frostType.serifReader.scaled()

        // code: monoBody with ink-alpha background (background applied via MarkdownColors)
        val code = frostType.monoBody.scaled()

        // quote: serifReader — blockQuote is further overridden via rememberFrostMarkdownComponents
        val quote = frostType.serifReader.scaled()

        // link span: ink + underline, monoBody weight
        val linkSpan =
            TextLinkStyles(
                style =
                    frostType.monoBody.copy(
                        textDecoration = TextDecoration.Underline,
                        color = ink,
                    ).toSpanStyle(),
            )

        DefaultMarkdownTypography(
            h1 = h1,
            h2 = h2,
            h3 = h3,
            h4 = h4,
            h5 = h5,
            h6 = h6,
            paragraph = body,
            text = body,
            quote = quote,
            code = code,
            inlineCode = code,
            bullet = frostType.monoBody.scaled(),
            list = frostType.monoBody.scaled(),
            ordered = frostType.monoBody.scaled(),
            textLink = linkSpan,
            table = frostType.monoBody.scaled(),
        )
    }
}

/**
 * Frost markdown component overrides.
 *
 * blockQuote renders the Frost [PullQuote] directly: ink leading bar + serifReader body.
 * All other components fall through to the library defaults (already styled via
 * [rememberFrostMarkdownTypography]).
 */
@Composable
fun rememberFrostMarkdownComponents(): MarkdownComponents {
    return remember {
        markdownComponents(
            blockQuote = { model ->
                val raw = model.node.getUnescapedTextInNode(model.content)
                PullQuote(text = raw)
            },
        )
    }
}
