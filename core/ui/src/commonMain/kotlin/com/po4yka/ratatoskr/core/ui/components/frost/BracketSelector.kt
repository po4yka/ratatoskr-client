@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
 * Frost bracket selector: horizontal row of bracket buttons; selected item gets ink fill + page text.
 *
 * Implements DESIGN.md § Components — BracketSelector (format toggle, type selection).
 */
@Composable
fun <T> BracketSelector(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
) {
    val ink = AppTheme.frostColors.ink
    val page = AppTheme.frostColors.page
    val separatorAlpha = AppTheme.border.separatorAlpha

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val bgColor = if (isSelected) ink else page
            val textColor = if (isSelected) page else ink
            val interactionSource = remember { MutableInteractionSource() }

            FrostText(
                text = "[ ${label(option).uppercase()} ]",
                style = AppTheme.frostType.monoEmph,
                color = textColor,
                modifier =
                    Modifier
                        .background(bgColor)
                        .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                        .clickable(
                            role = Role.RadioButton,
                            indication = FrostIndication,
                            interactionSource = interactionSource,
                            onClick = { onSelect(option) },
                        )
                        .padding(
                            horizontal = AppTheme.spacing.line,
                            vertical = AppTheme.spacing.cell,
                        ),
            )
        }
    }
}

@Preview
@Composable
private fun BracketSelectorPreview() {
    RatatoskrTheme {
        BracketSelector(
            options = listOf("Single", "Batch"),
            selected = "Single",
            onSelect = {},
        )
    }
}
