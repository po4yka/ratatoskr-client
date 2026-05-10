package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.remote.SyncApi
import com.po4yka.ratatoskr.data.remote.dto.SyncSessionRequestDto
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.time.Instant

private val logger = KotlinLogging.logger {}

internal data class SyncSessionInfo(
    val sessionId: String,
    val totalItems: Int?,
    val expiresAt: Instant?,
)

internal class SyncSessionCoordinator(private val api: SyncApi) {
    suspend fun create(): SyncSessionInfo {
        val response = api.createSession(null)
        if (response.success && response.data != null) {
            val data = requireNotNull(response.data)
            logger.info { "Created sync session: ${data.sessionId}, defaultLimit=${data.defaultLimit}" }
            val expiresAt =
                data.expiresAt?.let {
                    try {
                        Instant.parse(it)
                    } catch (_: Exception) {
                        null
                    }
                }
            return SyncSessionInfo(
                sessionId = data.sessionId,
                totalItems = data.defaultLimit,
                expiresAt = expiresAt,
            )
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to create sync session")
        }
    }

    suspend fun createWithLimit(limit: Int?): String {
        val request = limit?.let { SyncSessionRequestDto(limit = it) }
        val response = api.createSession(request)
        if (response.success && response.data != null) {
            val data = requireNotNull(response.data)
            logger.info { "Created sync session: ${data.sessionId}" }
            return data.sessionId
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to create sync session")
        }
    }

    suspend fun renew(
        currentSessionId: String,
        sessionExpiresAt: Instant?,
    ): Pair<String, Instant?> {
        if (sessionExpiresAt == null || Clock.System.now() < sessionExpiresAt) {
            return currentSessionId to sessionExpiresAt
        }
        logger.info { "Session expired, creating new session" }
        val newSession = create()
        return newSession.sessionId to newSession.expiresAt
    }
}
