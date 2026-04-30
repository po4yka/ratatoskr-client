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
 * Frost square checkbox: 16dp box with hairline border, diagonal check mark drawn via [drawBehind].
 *
 * Square shape, 0dp radius. Check mark is two diagonal lines forming a `✓`.
 * Implements DESIGN.md § Shapes — RectangleShape only, 0dp radius.
 */
@Composable
fun FrostCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val ink = AppTheme.frostColors.ink
    val separatorAlpha = AppTheme.border.separatorAlpha
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive
    val hairline = AppTheme.border.hairline
    val interactionSource = remember { MutableInteractionSource() }

    val checkModifier =
        if (checked) {
            Modifier.drawBehind {
                val stroke = hairline.toPx() * 2
                val w = size.width
                val h = size.height
                // Draw check mark: two lines forming ✓
                drawLine(
                    color = ink.copy(alpha = textAlpha),
                    start = Offset(w * 0.15f, h * 0.5f),
                    end = Offset(w * 0.4f, h * 0.75f),
                    strokeWidth = stroke,
                )
                drawLine(
                    color = ink.copy(alpha = textAlpha),
                    start = Offset(w * 0.4f, h * 0.75f),
                    end = Offset(w * 0.85f, h * 0.25f),
                    strokeWidth = stroke,
                )
            }
        } else {
            Modifier
        }

    val clickableModifier =
        if (onCheckedChange != null) {
            Modifier.clickable(
                enabled = enabled,
                role = Role.Checkbox,
                indication = FrostIndication,
                interactionSource = interactionSource,
                onClick = { onCheckedChange(!checked) },
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
                .then(checkModifier),
    )
}

@Preview
@Composable
private fun FrostCheckboxPreview() {
    RatatoskrTheme {
        FrostCheckbox(checked = true, onCheckedChange = {})
    }
}
