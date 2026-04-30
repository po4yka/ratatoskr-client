@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.frost

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.RatatoskrTheme

/**
 * Frost bracket text field: [BasicTextField] with `[` `]` literal decoration.
 *
 * monoBody style, ink color, hairline border, no corner radius.
 * Label rendered as monoXs uppercase above the field.
 * Implements DESIGN.md § Components — BracketField.
 */
@Composable
fun BracketField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    multiline: Boolean = false,
    leadingGlyph: String? = null,
) {
    val ink = AppTheme.frostColors.ink
    val separatorAlpha = AppTheme.border.separatorAlpha
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive

    Column(modifier) {
        FrostText(
            text = label.uppercase(),
            style = AppTheme.frostType.monoXs,
            color = ink.copy(alpha = AppTheme.alpha.secondary),
            modifier = Modifier.padding(bottom = AppTheme.spacing.gapInline),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = !multiline,
            textStyle = AppTheme.frostType.monoBody.copy(color = ink.copy(alpha = textAlpha)),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(AppTheme.border.hairline, ink.copy(alpha = separatorAlpha))
                    .padding(AppTheme.spacing.cell),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingGlyph != null) {
                        FrostText(
                            text = leadingGlyph,
                            style = AppTheme.frostType.monoBody,
                            color = ink.copy(alpha = AppTheme.alpha.secondary),
                            modifier = Modifier.padding(end = AppTheme.spacing.gapInline),
                        )
                    }
                    FrostText(
                        text = "[",
                        style = AppTheme.frostType.monoEmph,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                    innerTextField()
                    FrostText(
                        text = "]",
                        style = AppTheme.frostType.monoEmph,
                        color = ink.copy(alpha = AppTheme.alpha.secondary),
                    )
                }
            },
        )
    }
}

@Preview
@Composable
private fun BracketFieldPreview() {
    RatatoskrTheme {
        BracketField(
            value = "https://example.com",
            onValueChange = {},
            label = "URL",
            leadingGlyph = "→",
        )
    }
}
