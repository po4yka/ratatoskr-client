@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost pull quote: Source Serif 4 italic body with 4dp leading ink hairline bar.
 *
 * The leading bar is INK, not spark — pull quotes are editorial, not critical signal.
 * Attribution rendered in monoXs uppercase below the body.
 * Implements DESIGN.md § Components — Pull-Quote (Figma page 12, M23 · Highlights).
 */
@Composable
fun PullQuote(
    text: String,
    modifier: Modifier = Modifier,
    attribution: String? = null,
) {
    val ink = AppTheme.frostColors.ink
    val sparkBarWidth = AppTheme.border.sparkBar

    Column(
        modifier =
            modifier
                .drawBehind {
                    val barWidthPx = sparkBarWidth.toPx()
                    drawRect(
                        color = ink,
                        topLeft = Offset.Zero,
                        size = size.copy(width = barWidthPx),
                    )
                }
                .padding(start = AppTheme.spacing.line),
    ) {
        FrostText(
            text = text,
            style = AppTheme.frostType.serifReader,
            color = ink,
        )
        if (attribution != null) {
            FrostText(
                text = attribution.uppercase(),
                style = AppTheme.frostType.monoXs,
                color = ink.copy(alpha = AppTheme.alpha.secondary),
                modifier = Modifier.padding(top = AppTheme.spacing.cell),
            )
        }
    }
}

@Preview
@Composable
private fun PullQuotePreview() {
    RatatoskrTheme {
        PullQuote(
            text = "The medium is the message.",
            attribution = "Marshall McLuhan",
        )
    }
}
