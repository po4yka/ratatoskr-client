package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.annotation_dialog_helper
import ratatoskr.core.ui.generated.resources.annotation_dialog_placeholder
import ratatoskr.core.ui.generated.resources.annotation_dialog_save
import ratatoskr.core.ui.generated.resources.annotation_dialog_title
import ratatoskr.core.ui.generated.resources.collections_cancel
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun AnnotationDialog(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDialog(
        onDismissRequest = onCancel,
        title = stringResource(Res.string.annotation_dialog_title),
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(Res.string.collections_cancel))
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text(stringResource(Res.string.annotation_dialog_save))
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            TextArea(
                value = draft,
                onValueChange = onDraftChange,
                placeholderText = stringResource(Res.string.annotation_dialog_placeholder),
                helperText = stringResource(Res.string.annotation_dialog_helper),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6,
            )
        }
    }
}
