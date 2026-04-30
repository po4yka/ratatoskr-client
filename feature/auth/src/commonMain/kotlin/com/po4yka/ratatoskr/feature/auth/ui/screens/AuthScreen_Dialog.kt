package com.po4yka.ratatoskr.feature.auth.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
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
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.DeveloperCredentials
import com.po4yka.ratatoskr.core.ui.components.AppCheckbox
import com.po4yka.ratatoskr.core.ui.components.AppDialog
import com.po4yka.ratatoskr.core.ui.components.AppSmallSpinner
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.auth_developer_client_id_label
import ratatoskr.core.ui.generated.resources.auth_developer_client_id_placeholder
import ratatoskr.core.ui.generated.resources.auth_developer_login
import ratatoskr.core.ui.generated.resources.auth_developer_login_action
import ratatoskr.core.ui.generated.resources.auth_developer_remember_credentials
import ratatoskr.core.ui.generated.resources.auth_developer_secret_label
import ratatoskr.core.ui.generated.resources.auth_developer_secret_placeholder
import ratatoskr.core.ui.generated.resources.auth_developer_user_id_error
import ratatoskr.core.ui.generated.resources.auth_developer_user_id_label
import ratatoskr.core.ui.generated.resources.auth_developer_user_id_placeholder
import ratatoskr.core.ui.generated.resources.settings_cancel
import org.jetbrains.compose.resources.stringResource

/**
 * Developer login dialog.
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

    val userIdErrorText = stringResource(Res.string.auth_developer_user_id_error)

    AppDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = stringResource(Res.string.auth_developer_login),
    ) {
        // TODO: Phase D7 — migrate to BracketField once it supports keyboardOptions/isError/supportingText
        OutlinedTextField(
            value = userId,
            onValueChange = {
                userId = it
                isUserIdError = it.toIntOrNull() == null && it.isNotEmpty()
            },
            label = { FrostText(stringResource(Res.string.auth_developer_user_id_label)) },
            placeholder = { FrostText(stringResource(Res.string.auth_developer_user_id_placeholder)) },
            isError = isUserIdError,
            enabled = !isLoading,
            supportingText =
                if (isUserIdError) {
                    { FrostText(userIdErrorText) }
                } else {
                    null
                },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = clientId,
            onValueChange = { clientId = it },
            label = { FrostText(stringResource(Res.string.auth_developer_client_id_label)) },
            placeholder = { FrostText(stringResource(Res.string.auth_developer_client_id_placeholder)) },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = secret,
            onValueChange = { secret = it },
            label = { FrostText(stringResource(Res.string.auth_developer_secret_label)) },
            placeholder = { FrostText(stringResource(Res.string.auth_developer_secret_placeholder)) },
            enabled = !isLoading,
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
            AppCheckbox(
                checked = rememberCredentials,
                onCheckedChange = { rememberCredentials = it },
                enabled = !isLoading,
            )
            FrostText(
                text = stringResource(Res.string.auth_developer_remember_credentials),
                style = AppTheme.type.body01,
                color =
                    if (isLoading) {
                        AppTheme.colors.textDisabled
                    } else {
                        AppTheme.colors.textSecondary
                    },
            )
        }

        if (error != null) {
            FrostText(
                text = error,
                style = AppTheme.type.label01,
                color = AppTheme.colors.supportError,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BracketButton(
                label = stringResource(Res.string.settings_cancel),
                onClick = onDismiss,
                enabled = !isLoading,
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (isLoading) {
                AppSmallSpinner()
            } else {
                BracketButton(
                    label = stringResource(Res.string.auth_developer_login_action),
                    onClick = {
                        val uid = userId.toIntOrNull()
                        if (uid != null && clientId.isNotBlank() && secret.isNotBlank()) {
                            onLogin(uid, clientId, secret, rememberCredentials)
                        }
                    },
                    enabled =
                        !isLoading && !isUserIdError &&
                            userId.isNotBlank() && clientId.isNotBlank() && secret.isNotBlank(),
                )
            }
        }
    }
}
