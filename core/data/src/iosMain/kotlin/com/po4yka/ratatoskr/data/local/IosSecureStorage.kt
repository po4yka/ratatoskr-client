package com.po4yka.ratatoskr.data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.coroutines.toSuspendSettings

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
class IosSecureStorage : SecureStorage {
    private val settings = KeychainSettings(service = SERVICE_NAME).toSuspendSettings()

    override suspend fun saveAccessToken(token: String) {
        settings.putString(KEY_ACCESS_TOKEN, token)
    }

    override suspend fun getAccessToken(): String? {
        return settings.getStringOrNull(KEY_ACCESS_TOKEN)
    }

    override suspend fun saveRefreshToken(token: String) {
        settings.putString(KEY_REFRESH_TOKEN, token)
    }

    override suspend fun getRefreshToken(): String? {
        return settings.getStringOrNull(KEY_REFRESH_TOKEN)
    }

    override suspend fun clearTokens() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    override suspend fun saveDeveloperCredentials(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        settings.putInt(KEY_DEV_USER_ID, userId)
        settings.putString(KEY_DEV_CLIENT_ID, clientId)
        settings.putString(KEY_DEV_SECRET, secret)
    }

    override suspend fun getDeveloperCredentials(): DeveloperCredentials? {
        val userId = settings.getIntOrNull(KEY_DEV_USER_ID) ?: return null
        val clientId = settings.getStringOrNull(KEY_DEV_CLIENT_ID) ?: return null
        val secret = settings.getStringOrNull(KEY_DEV_SECRET) ?: return null
        return DeveloperCredentials(userId, clientId, secret)
    }

    override suspend fun clearDeveloperCredentials() {
        settings.remove(KEY_DEV_USER_ID)
        settings.remove(KEY_DEV_CLIENT_ID)
        settings.remove(KEY_DEV_SECRET)
    }

    private companion object {
        const val SERVICE_NAME = "com.po4yka.ratatoskr.auth"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_DEV_USER_ID = "developer_user_id"
        const val KEY_DEV_CLIENT_ID = "developer_client_id"
        const val KEY_DEV_SECRET = "developer_secret"
    }
}
