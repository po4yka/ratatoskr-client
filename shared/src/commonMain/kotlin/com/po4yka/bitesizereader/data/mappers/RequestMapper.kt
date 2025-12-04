package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.RequestDto
import com.po4yka.bitesizereader.database.RequestEntity
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import kotlin.time.Instant

fun RequestDto.toDomain(): Request {
    return Request(
        id = id,
        url = url,
        status = mapStatus(status),
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt)
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

fun RequestDto.toEntity(): RequestEntity {
    return RequestEntity(
        id = id,
        url = url,
        status = status,
        createdAt = Instant.parse(createdAt),
        updatedAt = Instant.parse(updatedAt)
    )
}

private fun mapStatus(status: String): RequestStatus {
    return try {
        RequestStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        RequestStatus.PENDING
    }
}
