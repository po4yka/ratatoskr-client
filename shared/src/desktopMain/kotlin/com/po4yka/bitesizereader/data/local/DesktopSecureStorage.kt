package com.po4yka.bitesizereader.data.local

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toSuspendSettings

class DesktopSecureStorage : SecureStorage {
    private val settings = MapSettings().toSuspendSettings()

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
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
