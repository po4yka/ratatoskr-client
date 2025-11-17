package com.po4yka.bitesizereader.data.local

/**
 * Desktop stub implementation of SecureStorage for Compose Hot Reload development
 * Uses in-memory storage (not secure, for development only)
 */
class DesktopSecureStorage : SecureStorage {
    private val storage = mutableMapOf<String, String>()

    override suspend fun saveString(
        key: String,
        value: String,
    ) {
        storage[key] = value
    }

    override suspend fun getString(key: String): String? = storage[key]

    override suspend fun remove(key: String) {
        storage.remove(key)
    }

    override suspend fun clear() {
        storage.clear()
    }

    override suspend fun saveAccessToken(token: String) {
        saveString("access_token", token)
    }

    override suspend fun getAccessToken(): String? = getString("access_token")

    override suspend fun saveRefreshToken(token: String) {
        saveString("refresh_token", token)
    }

    override suspend fun getRefreshToken(): String? = getString("refresh_token")

    override suspend fun clearTokens() {
        remove("access_token")
        remove("refresh_token")
    }
}
