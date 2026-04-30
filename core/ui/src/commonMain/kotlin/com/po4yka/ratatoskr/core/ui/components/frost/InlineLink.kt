@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost inline text link: monoEmph with hairline underline drawn via [drawBehind].
 *
 * No color change — ink at full alpha. Implements DESIGN.md § Components — InlineLink.
 */
@Composable
fun InlineLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    val hairline = AppTheme.border.hairline
    val interactionSource = remember { MutableInteractionSource() }

    FrostText(
        text = label,
        style = AppTheme.frostType.monoEmph,
        color = ink,
        modifier =
            modifier
                .drawBehind {
                    val strokeWidthPx = hairline.toPx()
                    val y = size.height - strokeWidthPx / 2f
                    drawLine(
                        color = ink,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidthPx,
                    )
                }
                .clickable(
                    role = Role.Button,
                    indication = FrostIndication,
                    interactionSource = interactionSource,
                    onClick = onClick,
                ),
    )
}

@Preview
@Composable
private fun InlineLinkPreview() {
    RatatoskrTheme {
        InlineLink(label = "DEV LOGIN", onClick = {})
    }
}
