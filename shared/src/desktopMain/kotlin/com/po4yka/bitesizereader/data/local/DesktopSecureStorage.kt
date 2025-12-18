package com.po4yka.bitesizereader.data.local

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Desktop implementation of [SecureStorage].
 *
 * SECURITY WARNING: This implementation is for DEVELOPMENT USE ONLY.
 * Tokens are stored in unencrypted in-memory storage (MapSettings) and are NOT persisted.
 * Do NOT use this implementation for production deployments with real user credentials.
 *
 * For production desktop applications, implement proper secure storage using:
 * - OS keychain integration (macOS Keychain, Windows Credential Manager)
 * - Encrypted file storage with hardware-backed keys
 */
class DesktopSecureStorage : SecureStorage {
    private val settings = MapSettings().toSuspendSettings()

    init {
        logger.warn {
            "DesktopSecureStorage: Using INSECURE in-memory storage. " +
                "This is for DEVELOPMENT ONLY. Tokens are not encrypted or persisted."
        }
    }

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
