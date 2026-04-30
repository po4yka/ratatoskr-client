@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost multi-select chip: monoSm uppercase, hairline border, ink-alpha fill when selected.
 *
 * No spark on selection state. Selected fill is ink at 0.08 alpha.
 * Implements DESIGN.md § Components — MultiSelectChip, Spark Accent Policy (forbidden on chips).
 */
@Composable
fun MultiSelectChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val ink = AppTheme.frostColors.ink
    val separatorAlpha = AppTheme.border.separatorAlpha
    val interactionSource = remember { MutableInteractionSource() }

    val bgColor = if (selected) ink.copy(alpha = 0.08f) else AppTheme.frostColors.page
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive

    FrostText(
        text = label.uppercase(),
        style = AppTheme.frostType.monoSm,
        color = ink.copy(alpha = textAlpha),
        modifier =
            modifier
                .background(bgColor)
                .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                .clickable(
                    enabled = enabled,
                    role = Role.Checkbox,
                    indication = FrostIndication,
                    interactionSource = interactionSource,
                    onClick = onToggle,
                )
                .padding(horizontal = AppTheme.spacing.cell, vertical = AppTheme.spacing.gapInline),
    )
}

@Preview
@Composable
private fun MultiSelectChipPreview() {
    RatatoskrTheme {
        MultiSelectChip(label = "Technology", selected = true, onToggle = {})
    }
}
