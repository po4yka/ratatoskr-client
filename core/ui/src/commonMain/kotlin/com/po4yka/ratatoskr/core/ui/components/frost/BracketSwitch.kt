package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIndication
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost bracket switch: renders `[X]` or `[ ]` followed by label text.
 *
 * No spark on checked state — checked state is the `[X]` literal.
 * Implements DESIGN.md § Components — BracketSwitch, Spark Accent Policy (forbidden on switches).
 */
@Composable
fun BracketSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val ink = AppTheme.frostColors.ink
    val interactionSource = remember { MutableInteractionSource() }
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive
    val glyph = if (checked) "[X]" else "[ ]"

    Row(
        modifier =
            modifier
                .clickable(
                    enabled = enabled,
                    role = Role.Switch,
                    indication = FrostIndication,
                    interactionSource = interactionSource,
                    onClick = { onCheckedChange(!checked) },
                )
                .padding(vertical = AppTheme.spacing.cell),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline),
    ) {
        FrostText(
            text = glyph,
            style = AppTheme.frostType.monoEmph,
            color = ink.copy(alpha = textAlpha),
        )
        FrostText(
            text = label,
            style = AppTheme.frostType.monoBody,
            color = ink.copy(alpha = textAlpha),
        )
    }
}

@Preview
@Composable
private fun BracketSwitchPreview() {
    RatatoskrTheme {
        BracketSwitch(checked = true, onCheckedChange = {}, label = "Enable notifications")
    }
}
