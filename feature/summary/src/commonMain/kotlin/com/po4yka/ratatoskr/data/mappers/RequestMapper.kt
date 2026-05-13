package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.SubmitRequestResponse
import com.po4yka.ratatoskr.database.RequestEntity
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.model.RequestStatus

fun SubmitRequestResponse.toDomain(url: String): Request {
    val effectiveId = requestId.toString()
    return Request(
        id = effectiveId,
        url = url,
        status = mapStatus(status.name),
        createdAt = createdAt,
        updatedAt = createdAt,
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

fun SubmitRequestResponse.toEntity(url: String): RequestEntity {
    val effectiveId = requestId.toString()
    return RequestEntity(
        id = effectiveId,
        url = url,
        status = status.name,
        createdAt = createdAt,
        updatedAt = createdAt,
    )
}

private fun mapStatus(status: String): RequestStatus {
    return try {
        RequestStatus.valueOf(status.uppercase())
    } catch (e: Exception) {
        RequestStatus.PENDING
    }
}
