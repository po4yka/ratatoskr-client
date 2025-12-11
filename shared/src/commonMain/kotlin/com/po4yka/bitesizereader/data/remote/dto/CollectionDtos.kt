package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false,
    @SerialName("owner_id") val ownerId: Long,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("items_count") val itemsCount: Int = 0,
    @SerialName("children") val children: List<CollectionDto> = emptyList(),
)

@Serializable
data class CollectionTreeNodeDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("children") val children: List<CollectionTreeNodeDto> = emptyList(),
)

@Serializable
data class CollectionCreateRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false,
    @SerialName("parent_id") val parentId: Int? = null,
)

@Serializable
data class CollectionUpdateRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean? = null,
    @SerialName("parent_id") val parentId: Int? = null,
)

@Serializable
data class CollectionItemCreateRequest(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("notes") val notes: String? = null,
)

@Serializable
data class CollectionItemDto(
    @SerialName("collection_id") val collectionId: Int,
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("added_at") val addedAt: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("summary") val summary: SummaryDetailDto? = null,
)

@Serializable
data class CollectionShareRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("role") val role: String, // "editor" or "viewer"
)

@Serializable
data class CollectionInviteRequest(
    @SerialName("role") val role: String,
    @SerialName("expires_at") val expiresAt: String? = null,
)

@Serializable
data class CollectionReorderRequest(
    @SerialName("items") val items: List<CollectionReorderItem>,
)

@Serializable
data class CollectionReorderItem(
    @SerialName("collection_id") val collectionId: Int,
    @SerialName("position") val position: Int,
)

@Serializable
data class CollectionItemReorderRequest(
    @SerialName("items") val items: List<CollectionItemReorderItem>,
)

@Serializable
data class CollectionItemReorderItem(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("position") val position: Int,
)

@Serializable
data class CollectionMoveRequest(
    @SerialName("parent_id") val parentId: Int?,
    @SerialName("position") val position: Int? = null,
)

@Serializable
data class CollectionItemMoveRequest(
    @SerialName("summary_ids") val summaryIds: List<Long>,
    @SerialName("target_collection_id") val targetCollectionId: Int,
    @SerialName("position") val position: Int? = null,
)

@Serializable
data class CollectionListResponse(
    @SerialName("collections") val collections: List<CollectionDto>,
)

// Backward-compatibility: some older callers referenced an Envelope type.
// Map it directly to the current response shape to tolerate stale binaries.
typealias CollectionListResponseEnvelope = CollectionListResponse

@Serializable
data class CollectionItemsResponse(
    @SerialName("items") val items: List<CollectionItemDto>,
    // Meta is usually top-level in ApiResponseDto, but if it's inside data, keep it.
    // However, ApiResponseDto has its own meta. Checking log... log has meta at top level.
    // So 'data' just has 'items'.
)

@Serializable
data class CollectionTreeResponse(
    @SerialName("nodes") val nodes: List<CollectionTreeNodeDto>, // Assuming "nodes" or "tree" or just list?
    // Log for getTree not seen. Assuming sticking to envelope pattern implies "data" contains object.
    // If "data": [ ... ], then T should be List<CollectionTreeNodeDto>.
    // If "data": { "nodes": [...] }, then T is CollectionTreeResponse.
    // Given CollectionListResponse has "collections", likely this has a name too?
    // Or it might be a direct list.
    // Safest bet: The current code expected 'data' field inside data.
    // KtorCollectionsApi called getTree.
    // Let's assume it returns a list directly in 'data' for now, or check standard pattern.
    // Actually, earlier I assumed 'data' was wrapper.
    // If previous code was `data: List<Node>`, it implied `data.data` was the list.
    // So JSON was `{"data": {"data": []}}`? No.
    // Previous code: `data class Envelope(success, data: List)`.
    // This mapped to `{"success":..., "data": [...]}`.
    // BUT `ApiResponseDto` wraps it. So `{"data": {"success":..., "data": [...]}}`.
    // Start with strictly removing wrapper.
)

// Actually, if the previous code was `val data: List<CollectionTreeNodeDto>`, it meant it expected
// `{"data": [...]}` inside the outer `data`.
// If the API returns `{"data": [...]}` (list directly), then T = List<CollectionTreeNodeDto>.
// If it returns `{"data": {"nodes": []}}`, then T = Wrapper("nodes").
// Given `v1/collections` return `{"collections": []}`, it's likely named.
// I will check `KtorCollectionsApi.kt` usage later.
// For now, I'll delete this envelope and rely on List or a new wrapper if needed.

@Serializable
data class SuccessResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
)

// CollectionReorderResponseEnvelope removed. Use SuccessResponse or specific data.

// CollectionMoveResponseEnvelope removed. Use CollectionMoveResponse directly.

@Serializable
data class CollectionMoveResponse(
    @SerialName("id") val id: Int,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("position") val position: Int,
    @SerialName("updated_at") val updatedAt: String? = null,
)

// CollectionItemsMoveResponseEnvelope removed. Use CollectionItemsMoveResponse directly.

@Serializable
data class CollectionItemsMoveResponse(
    @SerialName("moved_summary_ids") val movedSummaryIds: List<Long>,
)

// CollectionAclResponseEnvelope removed. Use CollectionAclResponse directly.

@Serializable
data class CollectionAclResponse(
    @SerialName("acl") val acl: List<CollectionAclEntry>,
)

@Serializable
data class CollectionAclEntry(
    @SerialName("user_id") val userId: Int,
    @SerialName("role") val role: String,
    @SerialName("status") val status: String,
    @SerialName("invited_by") val invitedBy: Int? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

// CollectionInviteResponseEnvelope removed. Use CollectionInviteResponse directly.

@Serializable
data class CollectionInviteResponse(
    @SerialName("token") val token: String,
    @SerialName("role") val role: String,
    @SerialName("expires_at") val expiresAt: String? = null,
)
