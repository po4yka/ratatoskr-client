package com.po4yka.ratatoskr.domain.model

/**
 * Represents a user session/device.
 */
data class Session(
    val id: Long,
    val clientId: String?,
    val deviceInfo: String?,
    val ipAddress: String?,
    val lastUsedAt: String?,
    val createdAt: String,
    val isCurrent: Boolean,
)
