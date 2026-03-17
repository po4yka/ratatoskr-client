package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.po4yka.bitesizereader.ui.theme.Spacing

@Suppress("FunctionNaming")
@Composable
fun AnnotationDialog(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = Carbon.theme.layer01,
        modifier = modifier,
        title = {
            Text(
                text = "Add note",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    placeholder = {
                        Text(
                            text = "Your note...",
                            style = Carbon.typography.bodyCompact01,
                            color = Carbon.theme.textSecondary,
                        )
                    },
                    maxLines = 6,
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Add a personal note to this highlight.",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.padding(bottom = Spacing.xs, end = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Button(
                    label = "Cancel",
                    onClick = onCancel,
                    buttonType = ButtonType.Ghost,
                )
                Button(
                    label = "Save",
                    onClick = onSave,
                )
            }
        },
        dismissButton = {},
    )
}
