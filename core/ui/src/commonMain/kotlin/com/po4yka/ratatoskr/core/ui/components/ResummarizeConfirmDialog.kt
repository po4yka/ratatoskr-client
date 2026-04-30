package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
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
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onDismiss,
            )
        },
        confirmButton = {
            BracketButton(
                label = stringResource(Res.string.resummarize_confirm_action),
                onClick = onConfirm,
            )
        },
    ) {
        FrostText(
            text = stringResource(Res.string.resummarize_confirm_message),
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
        )
    }
}
