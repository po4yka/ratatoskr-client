@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.*
import com.po4yka.bitesizereader.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Maps Request DTOs to domain models and vice versa
 */

fun RequestResponseDto.toDomain(): Request {
    return Request(
        id = requestId,
        inputUrl = "", // Not provided in response
        type = RequestType.URL,
        status = status.toRequestStatus(),
        stage = stage?.toProcessingStage(),
        progress = progress,
        langPreference = "auto",
        createdAt = Instant.parse(createdAt),
    )
}

fun RequestStatusDto.toDomain(): Request {
    return Request(
        id = requestId,
        inputUrl = "", // Not provided in response
        type = RequestType.URL,
        status = status.toRequestStatus(),
        stage = stage?.toProcessingStage(),
        progress = progress,
        langPreference = "auto",
        summaryId = summaryId,
        errorMessage = errorMessage,
        canRetry = canRetry,
        estimatedSecondsRemaining = estimatedSecondsRemaining,
        createdAt = Instant.DISTANT_PAST, // Not provided in status response
        updatedAt = Clock.System.now(),
    )
}

fun String.toRequestType(): RequestType {
    return when (this.lowercase()) {
        "url" -> RequestType.URL
        "youtube_video" -> RequestType.YOUTUBE_VIDEO
        else -> RequestType.URL
    }
}

fun String.toRequestStatus(): RequestStatus {
    return when (this.lowercase()) {
        "pending" -> RequestStatus.PENDING
        "processing" -> RequestStatus.PROCESSING
        "completed" -> RequestStatus.COMPLETED
        "error" -> RequestStatus.ERROR
        "cancelled" -> RequestStatus.CANCELLED
        else -> RequestStatus.PENDING
    }
}

fun String.toProcessingStage(): ProcessingStage {
    return when (this.lowercase()) {
        "content_extraction" -> ProcessingStage.CONTENT_EXTRACTION
        "llm_summarization" -> ProcessingStage.LLM_SUMMARIZATION
        "validation" -> ProcessingStage.VALIDATION
        "done" -> ProcessingStage.DONE
        else -> ProcessingStage.CONTENT_EXTRACTION
    }
}

// Domain to DTO conversions

fun String.toSubmitURLRequestDto(langPreference: String = "auto"): SubmitURLRequestDto {
    return SubmitURLRequestDto(
        inputUrl = this,
        langPreference = langPreference,
    )
}
