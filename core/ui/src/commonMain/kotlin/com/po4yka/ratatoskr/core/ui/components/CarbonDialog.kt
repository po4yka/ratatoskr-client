package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gabrieldrn.carbon.Carbon
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.Spacing

@Composable
fun CarbonDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable RowScope.() -> Unit)? = null,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .widthIn(max = Dimensions.dialogMaxWidth)
                        .clip(RoundedCornerShape(Dimensions.dialogCornerRadius))
                        .background(Carbon.theme.layer01)
                        .padding(Dimensions.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Text(
                    text = title,
                    style = Carbon.typography.heading03,
                    color = Carbon.theme.textPrimary,
                )

                content()

                if (dismissButton != null || confirmButton != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.dialogButtonSpacing),
                            content = {
                                dismissButton?.invoke(this)
                                confirmButton?.invoke(this)
                            },
                        )
                    }
                }
            }
        }
    }
}
