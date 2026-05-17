package com.po4yka.ratatoskr.feature.sync.api

import kotlinx.serialization.json.JsonObject

/**
 * Domain-shaped view of a sync item delivered to feature-owned appliers.
 *
 * The shared transport DTO (`data.remote.dto.SyncItemDto`) stays internal to
 * `core/data` and the sync repository's mapping layer — features only see this
 * sealed type, so the public `feature.sync.api` surface no longer leaks
 * kotlinx-serialization transport classes.
 *
 * Payload bodies remain typed as `JsonObject` so each feature continues to
 * decode the slice it cares about, without depending on a transport `data
 * class`.
 */
sealed interface SyncEntity {
    val id: String
    val entityType: String
    val serverVersion: Long
    val deletedAt: String?

    data class Summary(
        override val id: String,
        override val serverVersion: Long,
        override val deletedAt: String?,
        val payload: JsonObject?,
    ) : SyncEntity {
        override val entityType: String get() = ENTITY_TYPE_SUMMARY
    }

    data class Highlight(
        override val id: String,
        override val serverVersion: Long,
        override val deletedAt: String?,
        val payload: JsonObject?,
    ) : SyncEntity {
        override val entityType: String get() = ENTITY_TYPE_HIGHLIGHT
    }

    data class Tag(
        override val id: String,
        override val serverVersion: Long,
        override val deletedAt: String?,
        val payload: JsonObject?,
    ) : SyncEntity {
        override val entityType: String get() = ENTITY_TYPE_TAG
    }

    data class SummaryTag(
        override val id: String,
        override val serverVersion: Long,
        override val deletedAt: String?,
        val payload: JsonObject?,
    ) : SyncEntity {
        override val entityType: String get() = ENTITY_TYPE_SUMMARY_TAG
    }

    /** Entity types the mobile clients do not apply locally (e.g., server-only metrics). */
    data class Unknown(
        override val id: String,
        override val entityType: String,
        override val serverVersion: Long,
        override val deletedAt: String?,
    ) : SyncEntity

    companion object {
        const val ENTITY_TYPE_SUMMARY = "summary"
        const val ENTITY_TYPE_HIGHLIGHT = "highlight"
        const val ENTITY_TYPE_TAG = "tag"
        const val ENTITY_TYPE_SUMMARY_TAG = "summary_tag"
    }
}
