package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.SyncItemDto
import com.po4yka.ratatoskr.feature.sync.api.SyncEntity

internal fun SyncItemDto.toSyncEntity(): SyncEntity =
    when (entityType) {
        SyncEntity.ENTITY_TYPE_SUMMARY ->
            SyncEntity.Summary(
                id = idAsString,
                serverVersion = serverVersion,
                deletedAt = deletedAt,
                payload = summary,
            )
        SyncEntity.ENTITY_TYPE_HIGHLIGHT ->
            SyncEntity.Highlight(
                id = idAsString,
                serverVersion = serverVersion,
                deletedAt = deletedAt,
                payload = highlight,
            )
        SyncEntity.ENTITY_TYPE_TAG ->
            SyncEntity.Tag(
                id = idAsString,
                serverVersion = serverVersion,
                deletedAt = deletedAt,
                payload = tag,
            )
        SyncEntity.ENTITY_TYPE_SUMMARY_TAG ->
            SyncEntity.SummaryTag(
                id = idAsString,
                serverVersion = serverVersion,
                deletedAt = deletedAt,
                payload = summaryTag,
            )
        else ->
            SyncEntity.Unknown(
                id = idAsString,
                entityType = entityType,
                serverVersion = serverVersion,
                deletedAt = deletedAt,
            )
    }
