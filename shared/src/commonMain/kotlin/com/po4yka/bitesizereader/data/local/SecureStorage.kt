package com.po4yka.bitesizereader.data.local

/**
 * Secure storage interface for storing sensitive data like tokens
 */
interface SecureStorage {
    fun saveAccessToken(token: String)

    fun getAccessToken(): String?

    fun saveRefreshToken(token: String)

    fun getRefreshToken(): String?

    fun clearTokens()

    fun saveString(
        key: String,
        value: String,
    )

    fun getString(key: String): String?

    fun remove(key: String)
}

/**
 * Expect declaration for platform-specific secure storage
 */
expect class SecureStorageImpl : SecureStorage
