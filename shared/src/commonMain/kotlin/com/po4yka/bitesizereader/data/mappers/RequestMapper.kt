package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.RequestCreatedDto
import com.po4yka.bitesizereader.database.RequestEntity
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import kotlin.time.Clock
import kotlin.time.Instant

fun RequestCreatedDto.toDomain(url: String): Request {
    val effectiveId = (requestId ?: existingRequestId)?.toString()
        ?: throw IllegalArgumentException("Request ID missing in response")
    val createdInstant = createdAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
        ?: Clock.System.now()
    return Request(
        id = effectiveId,
        url = url,
        status = mapStatus(status),
        createdAt = createdInstant,
        updatedAt = createdInstant
    )
}

fun RequestEntity.toDomain(): Request {
    return Request(
        id = id,
        url = url,
        status = mapStatus(status),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RequestCreatedDto.toEntity(url: String): RequestEntity {
    val effectiveId = (requestId ?: existingRequestId)?.toString()
        ?: throw IllegalArgumentException("Request ID missing in response")
    val createdInstant = createdAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
        ?: Clock.System.now()
    return RequestEntity(
        id = effectiveId,
        url = url,
        status = status,
        createdAt = createdInstant,
        updatedAt = createdInstant
    )
}

private fun mapStatus(status: String): RequestStatus {
    return try {
        RequestStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        RequestStatus.PENDING
    }
}
