@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost square radio button: 16dp outer square with hairline border, 8dp inner square fill when selected.
 *
 * Square (no circle) — Frost has 0dp radius everywhere.
 * Implements DESIGN.md § Shapes — RectangleShape only.
 */
@Composable
fun FrostRadio(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val ink = AppTheme.frostColors.ink
    val separatorAlpha = AppTheme.border.separatorAlpha
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive
    val interactionSource = remember { MutableInteractionSource() }

    val innerFillModifier =
        if (selected) {
            Modifier.drawBehind {
                val inset = (size.width - 8.dp.toPx()) / 2f
                drawRect(
                    color = ink.copy(alpha = textAlpha),
                    topLeft = Offset(inset, inset),
                    size =
                        size.copy(
                            width = size.width - inset * 2,
                            height = size.height - inset * 2,
                        ),
                )
            }
        } else {
            Modifier
        }

    val clickableModifier =
        if (onClick != null) {
            Modifier.clickable(
                enabled = enabled,
                role = Role.RadioButton,
                indication = FrostIndication,
                interactionSource = interactionSource,
                onClick = onClick,
            )
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .size(16.dp)
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .then(clickableModifier)
                .then(innerFillModifier),
    )
}

@Preview
@Composable
private fun FrostRadioPreview() {
    RatatoskrTheme {
        FrostRadio(selected = true, onClick = {})
    }
}
