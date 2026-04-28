package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
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
    CarbonDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.resummarize_confirm_title),
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
                buttonType = ButtonType.Ghost,
            )
        },
        confirmButton = {
            Button(
                label = stringResource(Res.string.resummarize_confirm_action),
                onClick = onConfirm,
                buttonType = ButtonType.Primary,
            )
        },
    ) {
        Text(
            stringResource(Res.string.resummarize_confirm_message),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textSecondary,
        )
    }
}
