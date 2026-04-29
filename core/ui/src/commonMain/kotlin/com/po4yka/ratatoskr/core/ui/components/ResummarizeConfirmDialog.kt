package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.collections_cancel
import ratatoskr.core.ui.generated.resources.resummarize_confirm_action
import ratatoskr.core.ui.generated.resources.resummarize_confirm_message
import ratatoskr.core.ui.generated.resources.resummarize_confirm_title
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun ResummarizeConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.resummarize_confirm_title),
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.collections_cancel))
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(Res.string.resummarize_confirm_action))
            }
        },
    ) {
        Text(
            stringResource(Res.string.resummarize_confirm_message),
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textSecondary,
        )
    }
}
