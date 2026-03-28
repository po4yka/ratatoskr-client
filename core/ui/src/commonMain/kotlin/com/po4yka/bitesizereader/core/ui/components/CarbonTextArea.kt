package com.po4yka.bitesizereader.core.ui.components

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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.Spacing

@Composable
fun CarbonTextArea(
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
            Carbon.theme.supportError
        } else {
            Carbon.theme.borderSubtle00
        }
    val textColor =
        if (enabled) {
            Carbon.theme.textPrimary
        } else {
            Carbon.theme.textDisabled
        }
    val supportingTextColor =
        if (enabled) {
            Carbon.theme.textSecondary
        } else {
            Carbon.theme.textDisabled
        }
    val textStyle: TextStyle =
        Carbon.typography.bodyCompact01.copy(color = textColor)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                style = Carbon.typography.label01,
                color = supportingTextColor,
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = textStyle,
            cursorBrush = SolidColor(Carbon.theme.linkPrimary),
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
                                    Carbon.theme.background
                                } else {
                                    Carbon.theme.layer02
                                },
                            )
                            .border(Dimensions.borderWidth, borderColor, shape)
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                ) {
                    if (value.isEmpty() && !placeholderText.isNullOrBlank()) {
                        Text(
                            text = placeholderText,
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textPlaceholder,
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
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )

            !helperText.isNullOrBlank() ->
                Text(
                    text = helperText,
                    style = Carbon.typography.label01,
                    color = supportingTextColor,
                )
        }
    }
}
