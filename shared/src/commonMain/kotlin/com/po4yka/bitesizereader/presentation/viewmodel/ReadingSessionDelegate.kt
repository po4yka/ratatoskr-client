package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.usecase.EndReadingSessionUseCase
import com.po4yka.bitesizereader.domain.usecase.MarkSummaryAsReadUseCase
import com.po4yka.bitesizereader.domain.usecase.SaveReadPositionUseCase
import com.po4yka.bitesizereader.domain.usecase.StartReadingSessionUseCase
import com.po4yka.bitesizereader.presentation.state.ReadingSessionState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class ReadingSessionDelegate(
    private val startReadingSessionUseCase: StartReadingSessionUseCase,
    private val endReadingSessionUseCase: EndReadingSessionUseCase,
    private val saveReadPositionUseCase: SaveReadPositionUseCase,
    private val markSummaryAsReadUseCase: MarkSummaryAsReadUseCase,
) {
    private var activeSessionId: Long? = null
    private var sessionStartTime: Instant = Clock.System.now()
    private var lastScrollTime: Instant = Clock.System.now()
    private var isSessionPaused: Boolean = false

    companion object {
        private val INACTIVITY_THRESHOLD_MS = 5 * 60 * 1000L
    }

    suspend fun startSession(
        summaryId: String,
        isRead: Boolean,
        scope: CoroutineScope,
        onState: (ReadingSessionState) -> Unit,
    ) {
        if (activeSessionId != null) {
            endSessionFireAndForget(scope)
        }
        if (!isRead) {
            markSummaryAsReadUseCase(summaryId)
        }
        activeSessionId = startReadingSessionUseCase(summaryId)
        val now = Clock.System.now()
        sessionStartTime = now
        lastScrollTime = now
        isSessionPaused = false
        onState(ReadingSessionState(isSessionPaused = false, currentSessionDurationSec = 0))
    }

    fun endSession(
        scope: CoroutineScope,
        currentState: () -> ReadingSessionState,
        onState: (ReadingSessionState) -> Unit,
    ) {
        endSessionFireAndForget(scope)
        onState(currentState().copy(isSessionPaused = false, currentSessionDurationSec = 0))
    }

    fun notifyScrolled(
        currentState: () -> ReadingSessionState,
        onState: (ReadingSessionState) -> Unit,
    ) {
        val now = Clock.System.now()
        lastScrollTime = now
        if (isSessionPaused) {
            isSessionPaused = false
            sessionStartTime = now
            onState(currentState().copy(isSessionPaused = false))
        }
    }

    fun saveReadPosition(
        summaryId: String,
        position: Int,
        offset: Int,
        scope: CoroutineScope,
    ) {
        scope.launch {
            try {
                saveReadPositionUseCase(summaryId, position, offset)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.debug(e) { "Failed to save read position for $summaryId" }
            }
        }
    }

    fun checkInactivity(
        currentState: () -> ReadingSessionState,
        onState: (ReadingSessionState) -> Unit,
    ) {
        val now = Clock.System.now()
        if (!isSessionPaused && activeSessionId != null &&
            (now - lastScrollTime).inWholeMilliseconds > INACTIVITY_THRESHOLD_MS
        ) {
            isSessionPaused = true
            val durationSec = (lastScrollTime - sessionStartTime).inWholeSeconds.toInt().coerceAtLeast(0)
            onState(currentState().copy(isSessionPaused = true, currentSessionDurationSec = durationSec))
        }
    }

    private fun endSessionFireAndForget(scope: CoroutineScope) {
        val sessionId = activeSessionId ?: return
        activeSessionId = null
        val durationSec = calculateDurationSec()
        scope.launch {
            try {
                endReadingSessionUseCase(sessionId, durationSec)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                logger.debug(e) { "Failed to end reading session $sessionId" }
            }
        }
    }

    private fun calculateDurationSec(): Int {
        val now = Clock.System.now()
        return if (isSessionPaused) {
            (lastScrollTime - sessionStartTime).inWholeSeconds.toInt().coerceAtLeast(0)
        } else {
            (now - sessionStartTime).inWholeSeconds.toInt().coerceAtLeast(0)
        }
    }
}
