package com.po4yka.bitesizereader.feature.auth.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.gabrieldrn.carbon.textinput.TextInputState
import com.po4yka.bitesizereader.domain.model.DeveloperCredentials
import com.po4yka.bitesizereader.core.ui.components.CarbonCheckbox
import com.po4yka.bitesizereader.core.ui.components.CarbonDialog
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.auth_developer_client_id_label
import bitesizereader.core.ui.generated.resources.auth_developer_client_id_placeholder
import bitesizereader.core.ui.generated.resources.auth_developer_login
import bitesizereader.core.ui.generated.resources.auth_developer_login_action
import bitesizereader.core.ui.generated.resources.auth_developer_remember_credentials
import bitesizereader.core.ui.generated.resources.auth_developer_secret_label
import bitesizereader.core.ui.generated.resources.auth_developer_secret_placeholder
import bitesizereader.core.ui.generated.resources.auth_developer_user_id_error
import bitesizereader.core.ui.generated.resources.auth_developer_user_id_label
import bitesizereader.core.ui.generated.resources.auth_developer_user_id_placeholder
import bitesizereader.core.ui.generated.resources.settings_cancel
import org.jetbrains.compose.resources.stringResource

/**
 * Developer login dialog using Carbon Design System
 */
@Composable
fun DeveloperLoginDialog(
    isLoading: Boolean,
    error: String? = null,
    savedCredentials: DeveloperCredentials? = null,
    onDismiss: () -> Unit,
    onLogin: (userId: Int, clientId: String, secret: String, rememberCredentials: Boolean) -> Unit,
) {
    var userId by remember { mutableStateOf(savedCredentials?.userId?.toString() ?: "") }
    var clientId by remember { mutableStateOf(savedCredentials?.clientId ?: "") }
    var secret by remember { mutableStateOf(savedCredentials?.secret ?: "") }
    var isUserIdError by remember { mutableStateOf(false) }
    var rememberCredentials by remember { mutableStateOf(true) }

    CarbonDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = stringResource(Res.string.auth_developer_login),
    ) {
        TextInput(
            label = stringResource(Res.string.auth_developer_user_id_label),
            value = userId,
            onValueChange = {
                userId = it
                isUserIdError = it.toIntOrNull() == null && it.isNotEmpty()
            },
            state =
                when {
                    isUserIdError -> TextInputState.Error
                    isLoading -> TextInputState.Disabled
                    else -> TextInputState.Enabled
                },
            helperText =
                if (isUserIdError) {
                    stringResource(Res.string.auth_developer_user_id_error)
                } else {
                    ""
                },
            placeholderText = stringResource(Res.string.auth_developer_user_id_placeholder),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.auth_developer_client_id_label),
            value = clientId,
            onValueChange = { clientId = it },
            state = if (isLoading) TextInputState.Disabled else TextInputState.Enabled,
            placeholderText = stringResource(Res.string.auth_developer_client_id_placeholder),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )

        TextInput(
            label = stringResource(Res.string.auth_developer_secret_label),
            value = secret,
            onValueChange = { secret = it },
            state = if (isLoading) TextInputState.Disabled else TextInputState.Enabled,
            placeholderText = stringResource(Res.string.auth_developer_secret_placeholder),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        val uid = userId.toIntOrNull()
                        if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                            onLogin(uid, clientId, secret, rememberCredentials)
                        }
                    },
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isLoading) {
                        rememberCredentials = !rememberCredentials
                    },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CarbonCheckbox(
                checked = rememberCredentials,
                onCheckedChange = { rememberCredentials = it },
                enabled = !isLoading,
            )
            Text(
                text = stringResource(Res.string.auth_developer_remember_credentials),
                style = Carbon.typography.body01,
                color =
                    if (isLoading) {
                        Carbon.theme.textDisabled
                    } else {
                        Carbon.theme.textSecondary
                    },
            )
        }

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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                label = stringResource(Res.string.settings_cancel),
                onClick = onDismiss,
                isEnabled = !isLoading,
                buttonType = ButtonType.Secondary,
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (isLoading) {
                SmallLoading()
            } else {
                Button(
                    label = stringResource(Res.string.auth_developer_login_action),
                    onClick = {
                        val uid = userId.toIntOrNull()
                        if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                            onLogin(uid, clientId, secret, rememberCredentials)
                        }
                    },
                    isEnabled =
                        !isLoading && !isUserIdError &&
                            userId.isNotBlank() && clientId.isNotBlank() && secret.isNotBlank(),
                    buttonType = ButtonType.Primary,
                )
            }
        }
    }
}
