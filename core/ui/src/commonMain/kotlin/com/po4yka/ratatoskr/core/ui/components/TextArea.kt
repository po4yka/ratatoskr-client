package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing

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
    minHeight: androidx.compose.ui.unit.Dp = Dimensions.textAreaMinHeight,
    maxLines: Int = Int.MAX_VALUE,
) {
    val shape = RoundedCornerShape(Dimensions.cardCornerRadius)
    val borderColor =
        if (errorText != null) {
            AppTheme.colors.supportError
        } else {
            AppTheme.colors.borderSubtle00
        }
    val textColor =
        if (enabled) {
            AppTheme.colors.textPrimary
        } else {
            AppTheme.colors.textDisabled
        }
    val supportingTextColor =
        if (enabled) {
            AppTheme.colors.textSecondary
        } else {
            AppTheme.colors.textDisabled
        }
    val textStyle: TextStyle =
        AppTheme.type.bodyCompact01.copy(color = textColor)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = AppTheme.type.label01,
                color = supportingTextColor,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = textStyle,
            cursorBrush = SolidColor(AppTheme.colors.linkPrimary),
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = minHeight)
                            .clip(shape)
                            .background(
                                if (enabled) {
                                    AppTheme.colors.background
                                } else {
                                    AppTheme.colors.layer02
                                },
                            )
                            .border(Dimensions.borderWidth, borderColor, shape)
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                ) {
                    if (value.isEmpty() && !placeholderText.isNullOrBlank()) {
                        Text(
                            text = placeholderText,
                            style = AppTheme.type.bodyCompact01,
                            color = AppTheme.colors.textPlaceholder,
                        )
                    }

                    innerTextField()
                }
            },
        )

        when {
            errorText != null ->
                Text(
                    text = errorText,
                    style = AppTheme.type.label01,
                    color = AppTheme.colors.supportError,
                )

            !helperText.isNullOrBlank() ->
                Text(
                    text = helperText,
                    style = AppTheme.type.label01,
                    color = supportingTextColor,
                )
        }
    }
}
