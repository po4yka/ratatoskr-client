package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDialog
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.components.frost.BracketField
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import ratatoskr.core.ui.generated.resources.Res
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
    FrostDialog(
        onDismissRequest = onCancel,
        title = stringResource(Res.string.annotation_dialog_title),
        modifier = modifier,
        actions = {
            BracketButton(
                label = stringResource(Res.string.collections_cancel),
                onClick = onCancel,
            )
            BracketButton(
                label = stringResource(Res.string.annotation_dialog_save),
                onClick = onSave,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell)) {
            BracketField(
                value = draft,
                onValueChange = onDraftChange,
                label = stringResource(Res.string.annotation_dialog_placeholder),
                multiline = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
