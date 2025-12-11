package com.po4yka.bitesizereader.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.GeneralSecurityException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val logger = KotlinLogging.logger {}

private val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "secure_prefs_v3",
)

class AndroidSecureStorage(private val context: Context) : SecureStorage {
    private val aead: Aead by lazy { TinkKeyManager.getAead(context) }
    private val dataStore: DataStore<Preferences> get() = context.secureDataStore

    override suspend fun saveAccessToken(token: String) {
        saveEncrypted(KEY_ACCESS_TOKEN, token)
    }

    override suspend fun getAccessToken(): String? {
        return getDecrypted(KEY_ACCESS_TOKEN)
    }

    override suspend fun saveRefreshToken(token: String) {
        saveEncrypted(KEY_REFRESH_TOKEN, token)
    }

    override suspend fun getRefreshToken(): String? {
        return getDecrypted(KEY_REFRESH_TOKEN)
    }

    override suspend fun clearTokens() {
        dataStore.edit { it.clear() }
    }

    private suspend fun saveEncrypted(
        key: Preferences.Key<String>,
        value: String,
    ) {
        try {
            val encrypted = aead.encrypt(value.toByteArray(Charsets.UTF_8), null)
            val encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP)
            dataStore.edit { prefs ->
                prefs[key] = encoded
            }
        } catch (e: GeneralSecurityException) {
            logger.error(e) { "Failed to encrypt value for key: ${key.name}" }
            throw SecureStorageException("Encryption failed", e)
        }
    }

    private suspend fun getDecrypted(key: Preferences.Key<String>): String? {
        val encoded = dataStore.data.map { prefs -> prefs[key] }.first() ?: return null
        return try {
            val encrypted = Base64.decode(encoded, Base64.NO_WRAP)
            val decrypted = aead.decrypt(encrypted, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: GeneralSecurityException) {
            logger.error(e) { "Failed to decrypt value for key: ${key.name}" }
            null
        } catch (e: IllegalArgumentException) {
            // Base64 decoding failed - corrupted data
            logger.error(e) { "Failed to decode Base64 for key: ${key.name}" }
            null
        }
    }

    private companion object {
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}

/** Exception thrown when secure storage operations fail. */
class SecureStorageException(
    message: String,
    cause: Throwable,
) : RuntimeException(message, cause)
