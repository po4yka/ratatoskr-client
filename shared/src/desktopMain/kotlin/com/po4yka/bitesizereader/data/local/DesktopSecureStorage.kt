package com.po4yka.bitesizereader.data.local

class DesktopSecureStorage : SecureStorage {
    private val prefs = mutableMapOf<String, String>()

    override suspend fun saveAccessToken(token: String) {
        prefs["access_token"] = token
    }

    override suspend fun getAccessToken(): String? {
        return prefs["access_token"]
    }

    override suspend fun saveRefreshToken(token: String) {
        prefs["refresh_token"] = token
    }

    override suspend fun getRefreshToken(): String? {
        return prefs["refresh_token"]
    }

    override suspend fun clearTokens() {
        prefs.clear()
    }
}
