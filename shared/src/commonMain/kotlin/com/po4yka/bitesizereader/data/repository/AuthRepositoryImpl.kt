@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.local.SecureStorage
import com.po4yka.bitesizereader.data.mappers.*
import com.po4yka.bitesizereader.data.remote.api.AuthApi
import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.repository.AuthRepository
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Implementation of AuthRepository
 */
class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val secureStorage: SecureStorage,
) : AuthRepository {
    override suspend fun loginWithTelegram(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String,
    ): Result<Pair<AuthTokens, User>> {
        return try {
            logger.debug { "Attempting Telegram login for user: $telegramUserId" }
            val request =
                createTelegramLoginRequest(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = clientId,
                )

            val response = authApi.loginWithTelegram(request)

            if (response.success && response.data != null) {
                val (authTokens, user) = response.data.toDomain()

                // Store tokens securely
                storeTokens(authTokens)

                // Store user ID for later use
                secureStorage.saveString("user_id", user.id.toString())

                logger.info { "Successfully logged in user: ${user.id}" }
                Result.success(authTokens to user)
            } else {
                logger.warn { "Login failed: ${response.error?.message}" }
                Result.failure(Exception(response.error?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception during Telegram login for user: $telegramUserId" }
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthTokens> {
        return try {
            logger.debug { "Attempting to refresh access token" }
            val request = refreshToken.toTokenRefreshRequest()
            val response = authApi.refreshToken(request)

            if (response.success && response.data != null) {
                val authTokens = response.data.toAuthTokens()

                // Update stored tokens
                storeTokens(authTokens)

                logger.info { "Successfully refreshed access token" }
                Result.success(authTokens)
            } else {
                logger.warn { "Token refresh failed: ${response.error?.message}" }
                Result.failure(Exception(response.error?.message ?: "Token refresh failed"))
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception during token refresh" }
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = authApi.getCurrentUser()

            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        val accessToken = secureStorage.getAccessToken()
        val refreshToken = secureStorage.getRefreshToken()
        return !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()
    }

    override suspend fun getTokens(): Pair<String, String>? {
        val accessToken = secureStorage.getAccessToken()
        val refreshToken = secureStorage.getRefreshToken()

        return if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
            accessToken to refreshToken
        } else {
            null
        }
    }

    override suspend fun storeTokens(authTokens: AuthTokens) {
        secureStorage.saveAccessToken(authTokens.accessToken)
        secureStorage.saveRefreshToken(authTokens.refreshToken)

        // Store expiration time for token refresh logic
        secureStorage.saveString("token_expires_at", authTokens.expiresAt.toString())
    }

    override suspend fun logout() {
        logger.info { "Logging out user" }
        secureStorage.clearTokens()
        secureStorage.remove("user_id")
        secureStorage.remove("token_expires_at")
        logger.debug { "User logged out successfully" }
    }
}
