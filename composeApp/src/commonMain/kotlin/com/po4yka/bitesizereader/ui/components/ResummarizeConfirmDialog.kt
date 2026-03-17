package com.po4yka.bitesizereader.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType

@Suppress("FunctionNaming")
@Composable
fun ResummarizeConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Carbon.theme.layer01,
        title = {
            Text("Re-summarize?", style = Carbon.typography.heading03, color = Carbon.theme.textPrimary)
        },
        text = {
            Text(
                "This will replace the current summary with a fresh one. This cannot be undone.",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        },
        confirmButton = {
            Button(label = "Re-summarize", onClick = onConfirm, buttonType = ButtonType.Primary)
        },
        dismissButton = {
            Button(label = "Cancel", onClick = onDismiss, buttonType = ButtonType.Ghost)
        },
    )
}
