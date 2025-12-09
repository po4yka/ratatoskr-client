package com.po4yka.bitesizereader.data.local

interface SecureStorage {
    suspend fun saveAccessToken(token: String)

    suspend fun getAccessToken(): String?

    suspend fun saveRefreshToken(token: String)

    suspend fun getRefreshToken(): String?

    suspend fun saveSessionId(sessionId: Long)

    suspend fun getSessionId(): Long?

    suspend fun clearTokens()
}
