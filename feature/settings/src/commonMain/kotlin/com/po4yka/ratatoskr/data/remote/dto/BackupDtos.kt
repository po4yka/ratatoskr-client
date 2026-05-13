package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Decode-intermediary for the backup restore response envelope.
 *
 * The generated OpenAPI client exposes `BackupRestoreData` as a
 * `JsonElement` typealias because the backend schema declares it as a
 * free-form object. We decode the relevant fields locally until the
 * upstream contract is typed.
 */
@Serializable
data class BackupRestoreResponseDto(
    @SerialName("restored") val restored: Int,
    @SerialName("skipped") val skipped: Int,
    @SerialName("errors") val errors: List<String> = emptyList(),
)
