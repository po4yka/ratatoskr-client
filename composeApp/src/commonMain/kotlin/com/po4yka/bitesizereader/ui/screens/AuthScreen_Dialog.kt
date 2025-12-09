package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState

/**
 * Developer login dialog using Carbon Design System
 */
@Composable
fun DeveloperLoginDialog(
    isLoading: Boolean,
    error: String? = null,
    onDismiss: () -> Unit,
    onLogin: (Int, String, String) -> Unit,
) {
    var userId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    var isUserIdError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Carbon.theme.layer01)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Developer Login",
                style = Carbon.typography.heading03,
                color = Carbon.theme.textPrimary,
            )

            TextInput(
                label = "User ID",
                value = userId,
                onValueChange = {
                    userId = it
                    isUserIdError = it.toIntOrNull() == null && it.isNotEmpty()
                },
                state = if (isUserIdError) TextInputState.Error else if (isLoading) TextInputState.Disabled else TextInputState.Enabled,
                helperText = if (isUserIdError) "User ID must be a number" else "",
                placeholderText = "Enter user ID",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            TextInput(
                label = "Client ID",
                value = clientId,
                onValueChange = { clientId = it },
                state = if (isLoading) TextInputState.Disabled else TextInputState.Enabled,
                placeholderText = "Enter client ID",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )

            TextInput(
                label = "Secret",
                value = secret,
                onValueChange = { secret = it },
                state = if (isLoading) TextInputState.Disabled else TextInputState.Enabled,
                placeholderText = "Enter secret",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val uid = userId.toIntOrNull()
                        if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                            onLogin(uid, clientId, secret)
                        }
                    },
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            if (error != null) {
                Text(
                    text = error,
                    style = Carbon.typography.label01,
                    color = Carbon.theme.supportError,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Cancel",
                    onClick = onDismiss,
                    isEnabled = !isLoading,
                    buttonType = ButtonType.Secondary,
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isLoading) {
                    SmallLoading()
                } else {
                    Button(
                        label = "Login",
                        onClick = {
                            val uid = userId.toIntOrNull()
                            if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                                onLogin(uid, clientId, secret)
                            }
                        },
                        isEnabled = !isLoading && !isUserIdError &&
                            userId.isNotBlank() && clientId.isNotBlank() && secret.isNotBlank(),
                        buttonType = ButtonType.Primary,
                    )
                }
            }
        }
    }
}
