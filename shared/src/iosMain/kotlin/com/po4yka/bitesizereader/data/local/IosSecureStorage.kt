package com.po4yka.bitesizereader.data.local

import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.coroutines.toSuspendSettings

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
        settings.clear()
    }

    private companion object {
        const val SERVICE_NAME = "com.po4yka.bitesizereader.auth"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
