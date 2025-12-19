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
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

    override suspend fun saveDeveloperCredentials(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        saveEncrypted(KEY_DEV_USER_ID, userId.toString())
        saveEncrypted(KEY_DEV_CLIENT_ID, clientId)
        saveEncrypted(KEY_DEV_SECRET, secret)
    }

    override suspend fun getDeveloperCredentials(): DeveloperCredentials? {
        val userId = getDecrypted(KEY_DEV_USER_ID)?.toIntOrNull() ?: return null
        val clientId = getDecrypted(KEY_DEV_CLIENT_ID) ?: return null
        val secret = getDecrypted(KEY_DEV_SECRET) ?: return null
        return DeveloperCredentials(userId, clientId, secret)
    }

    override suspend fun clearDeveloperCredentials() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_DEV_USER_ID)
            prefs.remove(KEY_DEV_CLIENT_ID)
            prefs.remove(KEY_DEV_SECRET)
        }
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
        val KEY_DEV_USER_ID = stringPreferencesKey("developer_user_id")
        val KEY_DEV_CLIENT_ID = stringPreferencesKey("developer_client_id")
        val KEY_DEV_SECRET = stringPreferencesKey("developer_secret")
    }
}

/** Exception thrown when secure storage operations fail. */
class SecureStorageException(
    message: String,
    cause: Throwable,
) : RuntimeException(message, cause)
