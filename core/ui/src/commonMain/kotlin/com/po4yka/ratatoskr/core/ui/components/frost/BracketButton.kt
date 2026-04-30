@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
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
 * Frost bracketed button: renders `[ LABEL ]` in monoEmph uppercase with hairline border.
 *
 * Critical variant adds a 4dp leading spark bar. Brackets are text characters, not a Surface.
 * Implements DESIGN.md § Components — Bracket-Button.
 */
enum class BracketButtonVariant { Default, Critical }

@Composable
fun BracketButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: BracketButtonVariant = BracketButtonVariant.Default,
) {
    val ink = AppTheme.frostColors.ink
    val spark = AppTheme.frostColors.spark
    val separatorAlpha = AppTheme.border.separatorAlpha
    val sparkBarWidth = AppTheme.border.sparkBar
    val interactionSource = remember { MutableInteractionSource() }

    val sparkModifier =
        if (variant == BracketButtonVariant.Critical) {
            Modifier.drawBehind {
                val barWidthPx = sparkBarWidth.toPx()
                drawRect(
                    color = spark,
                    topLeft = Offset.Zero,
                    size = size.copy(width = barWidthPx),
                )
            }
        } else {
            Modifier
        }

    val alphaValue = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive

    FrostText(
        text = "[ ${label.uppercase()} ]",
        style = AppTheme.frostType.monoEmph,
        color = ink.copy(alpha = alphaValue),
        modifier =
            modifier
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .then(sparkModifier)
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    indication = FrostIndication,
                    interactionSource = interactionSource,
                    onClick = onClick,
                )
                .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.cell),
    )
}

@Preview
@Composable
private fun BracketButtonPreview() {
    RatatoskrTheme {
        BracketButton(label = "Submit", onClick = {}, variant = BracketButtonVariant.Critical)
    }
}
