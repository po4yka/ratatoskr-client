package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.delete_account_confirmation_label
import bitesizereader.core.ui.generated.resources.delete_account_confirmation_keyword
import bitesizereader.core.ui.generated.resources.delete_account_confirmation_prompt
import bitesizereader.core.ui.generated.resources.delete_account_confirmation_warning
import bitesizereader.core.ui.generated.resources.delete_account_deleting
import bitesizereader.core.ui.generated.resources.settings_cancel
import bitesizereader.core.ui.generated.resources.settings_delete_account
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

    CarbonDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = stringResource(Res.string.settings_delete_account),
    ) {
        Text(
            text = stringResource(Res.string.delete_account_confirmation_warning),
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
        )

        Text(
            text = stringResource(Res.string.delete_account_confirmation_prompt, confirmationText),
            style = Carbon.typography.label01,
            color = Carbon.theme.textSecondary,
        )

        TextInput(
            label = stringResource(Res.string.delete_account_confirmation_label),
            value = confirmationInput,
            onValueChange = { confirmationInput = it },
            placeholderText = confirmationText,
            state = if (error != null) TextInputState.Error else TextInputState.Enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        error?.let {
            Text(
                text = it,
                style = Carbon.typography.label01,
                color = Carbon.theme.supportError,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                label = stringResource(Res.string.settings_cancel),
                onClick = onDismiss,
                isEnabled = !isDeleting,
                buttonType = ButtonType.Secondary,
                modifier = Modifier.weight(1f),
            )
            Button(
                label =
                    if (isDeleting) {
                        stringResource(Res.string.delete_account_deleting)
                    } else {
                        stringResource(Res.string.settings_delete_account)
                    },
                onClick = onConfirm,
                isEnabled = canConfirm,
                buttonType = ButtonType.PrimaryDanger,
                modifier = Modifier.weight(1f),
            )
        }

        if (isDeleting) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                SmallLoading()
            }
        }
    }
}
