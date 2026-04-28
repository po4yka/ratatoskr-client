package com.po4yka.ratatoskr.data.local

/**
 * Developer credentials for secret-based login.
 */
data class DeveloperCredentials(
    val userId: Int,
    val clientId: String,
    val secret: String,
)

interface SecureStorage {
    suspend fun saveAccessToken(token: String)

    suspend fun getAccessToken(): String?

    suspend fun saveRefreshToken(token: String)

    suspend fun getRefreshToken(): String?

    suspend fun clearTokens()

    // Developer credentials methods
    suspend fun saveDeveloperCredentials(
        userId: Int,
        clientId: String,
        secret: String,
    )

    suspend fun getDeveloperCredentials(): DeveloperCredentials?

    suspend fun clearDeveloperCredentials()
}
