package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

@Composable
fun TextArea(
    label: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    minHeight: Dp = 100.dp,
    maxLines: Int = Int.MAX_VALUE,
) {
    val ink = AppTheme.frostColors.ink
    val borderColor =
        if (errorText != null) {
            AppTheme.frostColors.spark
        } else {
            ink.copy(alpha = AppTheme.border.separatorAlpha)
        }
    val textAlpha = if (enabled) AppTheme.alpha.active else AppTheme.alpha.inactive
    val supportingAlpha = if (enabled) AppTheme.alpha.secondary else AppTheme.alpha.inactive

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.gapInline),
    ) {
        if (!label.isNullOrBlank()) {
            FrostText(
                text = label,
                style = AppTheme.frostType.monoSm,
                color = ink.copy(alpha = supportingAlpha),
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = AppTheme.frostType.monoBody.copy(color = ink.copy(alpha = textAlpha)),
            cursorBrush = SolidColor(ink),
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = minHeight)
                            .background(AppTheme.frostColors.page, RectangleShape)
                            .border(AppTheme.border.hairline, borderColor, RectangleShape)
                            .padding(horizontal = AppTheme.spacing.cell, vertical = AppTheme.spacing.cell),
                ) {
                    if (value.isEmpty() && !placeholderText.isNullOrBlank()) {
                        FrostText(
                            text = placeholderText,
                            style = AppTheme.frostType.monoBody,
                            color = ink.copy(alpha = AppTheme.alpha.inactive),
                        )
                    }
                    innerTextField()
                }
            },
        )

        when {
            errorText != null ->
                FrostText(
                    text = errorText,
                    style = AppTheme.frostType.monoSm,
                    color = ink, // Never red text per Frost spark policy
                )

            !helperText.isNullOrBlank() ->
                FrostText(
                    text = helperText,
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = supportingAlpha),
                )
        }
    }
}
