package com.po4yka.bitesizereader.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun DeveloperLoginDialog(
    isLoading: Boolean,
    error: String? = null,
    onDismiss: () -> Unit,
    onLogin: (Int, String, String) -> Unit
) {
    var userId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    var isUserIdError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("Developer Login") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = {
                        userId = it
                        isUserIdError = it.toIntOrNull() == null
                    },
                    label = { Text("User ID") },
                    isError = isUserIdError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !isLoading
                )
                if (isUserIdError) {
                    Text("User ID must be a number", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = clientId,
                    onValueChange = { clientId = it },
                    label = { Text("Client ID") },
                    singleLine = true,
                    enabled = !isLoading
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text("Secret") },
                    singleLine = true,
                    enabled = !isLoading
                )
                if (error != null) {
                   Text(
                       text = error,
                       color = MaterialTheme.colorScheme.error,
                       style = MaterialTheme.typography.bodySmall,
                       modifier = androidx.compose.ui.Modifier.padding(top = 8.dp)
                   )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uid = userId.toIntOrNull()
                    if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                        onLogin(uid, clientId, secret)
                    }
                },
                enabled = !isLoading && !isUserIdError && userId.isNotBlank() && clientId.isNotBlank() && secret.isNotBlank()
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = androidx.compose.ui.Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}
