package com.po4yka.bitesizereader.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.toSuspendSettings

class AndroidSecureStorage(context: Context) : SecureStorage {
    private val masterKey =
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val encryptedPrefs =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    private val settings = SharedPreferencesSettings(encryptedPrefs).toSuspendSettings()

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

    override suspend fun saveSessionId(sessionId: Long) {
        settings.putLong(KEY_SESSION_ID, sessionId)
    }

    override suspend fun getSessionId(): Long? {
        return settings.getLongOrNull(KEY_SESSION_ID)
    }

    override suspend fun clearTokens() {
        settings.clear()
    }

    private companion object {
        const val PREFS_NAME = "secure_prefs_v2"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_SESSION_ID = "session_id"
    }
}
