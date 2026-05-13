package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.SyncApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.SyncSessionRequest
import com.po4yka.ratatoskr.api.generated.models.SyncSessionResponseEnvelope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.time.Instant

private val logger = KotlinLogging.logger {}

internal data class SyncSessionInfo(
    val sessionId: String,
    val totalItems: Int?,
    val expiresAt: Instant?,
)

internal class SyncSessionCoordinator {
    suspend fun create(): SyncSessionInfo = create(limit = null)

    suspend fun createWithLimit(limit: Int?): String = create(limit = limit).sessionId

    private suspend fun create(limit: Int?): SyncSessionInfo {
        val body = limit?.let { SyncSessionRequest(limit = it.toLong()) }
        val envelope: SyncSessionResponseEnvelope =
            SyncApi.createSyncSessionV1SyncSessionsPost(body = body).unwrap()
        val data =
            envelope.data
                ?: throw IllegalStateException(
                    "Failed to create sync session: server returned no data",
                )
        logger.info { "Created sync session: ${data.sessionId}, defaultLimit=${data.defaultLimit}" }
        return SyncSessionInfo(
            sessionId = data.sessionId,
            totalItems = data.defaultLimit.toInt(),
            expiresAt = data.expiresAt,
        )
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
