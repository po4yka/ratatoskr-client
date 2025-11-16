package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model representing a summarization request.
 * Tracks the processing of submitted URLs.
 */
data class Request(
    val id: Int,
    val inputUrl: String,
    val type: RequestType,
    val status: RequestStatus,
    val stage: ProcessingStage?,
    val progress: Int, // 0-100
    val langPreference: String,
    // Result
    val summaryId: Int? = null,
    // Error handling
    val errorMessage: String? = null,
    val canRetry: Boolean = false,
    // Progress estimation
    val estimatedSecondsRemaining: Int? = null,
    // Timestamps
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val completedAt: Instant? = null,
)

/**
 * Type of content being summarized
 */
enum class RequestType {
    URL,
    YOUTUBE_VIDEO,
}

/**
 * Current processing status
 */
enum class RequestStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    ERROR,
    CANCELLED,
}

/**
 * Processing stage for progress tracking
 */
enum class ProcessingStage {
    CONTENT_EXTRACTION,
    LLM_SUMMARIZATION,
    VALIDATION,
    DONE,
}
