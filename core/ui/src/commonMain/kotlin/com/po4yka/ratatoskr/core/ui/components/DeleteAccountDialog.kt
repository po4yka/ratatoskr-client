package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.delete_account_confirmation_label
import ratatoskr.core.ui.generated.resources.delete_account_confirmation_keyword
import ratatoskr.core.ui.generated.resources.delete_account_confirmation_prompt
import ratatoskr.core.ui.generated.resources.delete_account_confirmation_warning
import ratatoskr.core.ui.generated.resources.delete_account_deleting
import ratatoskr.core.ui.generated.resources.settings_cancel
import ratatoskr.core.ui.generated.resources.settings_delete_account
import org.jetbrains.compose.resources.stringResource

/**
 * Dialog for confirming account deletion.
 * Requires user to type "DELETE" to confirm.
 */
@Suppress("FunctionNaming")
@Composable
fun DeleteAccountDialog(
    isDeleting: Boolean,
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val confirmationText = stringResource(Res.string.delete_account_confirmation_keyword)
    var confirmationInput by remember { mutableStateOf("") }
    val canConfirm = confirmationInput == confirmationText && !isDeleting

    AppDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = stringResource(Res.string.settings_delete_account),
    ) {
        Text(
            text = stringResource(Res.string.delete_account_confirmation_warning),
            style = AppTheme.type.bodyCompact01,
            color = AppTheme.colors.textPrimary,
        )

        Text(
            text = stringResource(Res.string.delete_account_confirmation_prompt, confirmationText),
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
        )

        OutlinedTextField(
            value = confirmationInput,
            onValueChange = { confirmationInput = it },
            label = { Text(stringResource(Res.string.delete_account_confirmation_label)) },
            placeholder = { Text(confirmationText) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isDeleting,
                modifier = Modifier.weight(1f),
            ) {
                Text(stringResource(Res.string.settings_cancel))
            }
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.supportError,
                        contentColor = AppTheme.colors.textOnColor,
                    ),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    if (isDeleting) {
                        stringResource(Res.string.delete_account_deleting)
                    } else {
                        stringResource(Res.string.settings_delete_account)
                    },
                )
            }
        }

        if (isDeleting) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                AppSmallSpinner()
            }
        }
    }
}
