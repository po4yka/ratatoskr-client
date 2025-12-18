package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState

private const val CONFIRMATION_TEXT = "DELETE"

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
    var confirmationInput by remember { mutableStateOf("") }
    val canConfirm = confirmationInput == CONFIRMATION_TEXT && !isDeleting

    Dialog(onDismissRequest = { if (!isDeleting) onDismiss() }) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Carbon.theme.layer01)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Delete Account",
                style = Carbon.typography.heading03,
                color = Carbon.theme.supportError,
            )

            Text(
                text =
                    "This action is permanent and cannot be undone. " +
                        "All your data, including summaries and settings, will be permanently deleted.",
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textPrimary,
            )

            Text(
                text = "Type \"$CONFIRMATION_TEXT\" to confirm:",
                style = Carbon.typography.label01,
                color = Carbon.theme.textSecondary,
            )

            TextInput(
                label = "Confirmation",
                value = confirmationInput,
                onValueChange = { confirmationInput = it },
                placeholderText = CONFIRMATION_TEXT,
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
                    label = "Cancel",
                    onClick = onDismiss,
                    isEnabled = !isDeleting,
                    buttonType = ButtonType.Secondary,
                    modifier = Modifier.weight(1f),
                )
                Button(
                    label = if (isDeleting) "Deleting..." else "Delete Account",
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
}
