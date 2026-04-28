package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.SubmitRequestResponseDto
import com.po4yka.ratatoskr.database.RequestEntity
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus
import kotlin.time.Clock
import kotlin.time.Instant

fun SubmitRequestResponseDto.toDomain(url: String): Request {
    val effectiveId = requestId.toString()
    val createdInstant =
        runCatching { Instant.parse(createdAt) }.getOrNull()
            ?: Clock.System.now()
    return Request(
        id = effectiveId,
        url = url,
        status = mapStatus(status),
        createdAt = createdInstant,
        updatedAt = createdInstant,
    )
}

fun RequestEntity.toDomain(): Request {
    return Request(
        id = id,
        url = url,
        status = mapStatus(status),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun SubmitRequestResponseDto.toEntity(url: String): RequestEntity {
    val effectiveId = requestId.toString()
    val createdInstant =
        runCatching { Instant.parse(createdAt) }.getOrNull()
            ?: Clock.System.now()
    return RequestEntity(
        id = effectiveId,
        url = url,
        status = status,
        createdAt = createdInstant,
        updatedAt = createdInstant,
    )
}

private fun mapStatus(status: String): RequestStatus {
    return try {
        RequestStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        RequestStatus.PENDING
    }
}
