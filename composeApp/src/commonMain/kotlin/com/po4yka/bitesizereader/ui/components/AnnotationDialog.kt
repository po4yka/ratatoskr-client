package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.annotation_dialog_helper
import bitesizereader.composeapp.generated.resources.annotation_dialog_placeholder
import bitesizereader.composeapp.generated.resources.annotation_dialog_save
import bitesizereader.composeapp.generated.resources.annotation_dialog_title
import bitesizereader.composeapp.generated.resources.collections_cancel
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
    CarbonDialog(
        onDismissRequest = onCancel,
        title = stringResource(Res.string.annotation_dialog_title),
        modifier = modifier,
        dismissButton = {
            Button(
                label = stringResource(Res.string.collections_cancel),
                onClick = onCancel,
                buttonType = ButtonType.Ghost,
            )
        },
        confirmButton = {
            Button(
                label = stringResource(Res.string.annotation_dialog_save),
                onClick = onSave,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            CarbonTextArea(
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
