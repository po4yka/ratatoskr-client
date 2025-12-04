package com.po4yka.bitesizereader.data.local

import platform.Foundation.NSUserDefaults

class IosSecureStorage : SecureStorage {
    // FIXME: Use Keychain for production! This is insecure.
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun saveAccessToken(token: String) {
        userDefaults.setObject(token, "access_token")
    }

    override suspend fun getAccessToken(): String? {
        return userDefaults.stringForKey("access_token")
    }

    override suspend fun saveRefreshToken(token: String) {
        userDefaults.setObject(token, "refresh_token")
    }

    override suspend fun getRefreshToken(): String? {
        return userDefaults.stringForKey("refresh_token")
    }

    override suspend fun clearTokens() {
        userDefaults.removeObjectForKey("access_token")
        userDefaults.removeObjectForKey("refresh_token")
    }
}
