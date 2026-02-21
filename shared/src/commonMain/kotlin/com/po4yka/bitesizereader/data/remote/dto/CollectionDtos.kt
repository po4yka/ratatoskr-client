package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Collection data matching OpenAPI Collection schema.
 */
@Serializable
data class CollectionDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("position") val position: Int? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("server_version") val serverVersion: Int? = null,
    @SerialName("is_shared") val isShared: Boolean = false,
    @SerialName("share_count") val shareCount: Int? = null,
    @SerialName("item_count") val itemCount: Int? = null,
    @SerialName("children") val children: List<CollectionDto>? = null,
    @SerialName("acl_summary") val aclSummary: AclSummaryDto? = null,
)

/**
 * ACL summary showing collaborator counts by role.
 */
@Serializable
data class AclSummaryDto(
    @SerialName("total_collaborators") val totalCollaborators: Int,
    @SerialName("roles") val roles: List<String> = emptyList(),
)

// CollectionTreeNodeDto removed -- spec uses Collection for tree nodes

@Serializable
data class CollectionCreateRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("position") val position: Int? = null,
)

@Serializable
data class CollectionUpdateRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("position") val position: Int? = null,
)

@Serializable
data class CollectionItemCreateRequest(
    @SerialName("summary_id") val summaryId: Int,
)

@Serializable
data class CollectionItemDto(
    @SerialName("collection_id") val collectionId: Int,
    @SerialName("summary_id") val summaryId: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("position") val position: Int? = null,
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
    @SerialName("collections") val collections: List<CollectionDto>,
)

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
    @SerialName("role") val role: String,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("invited_by") val invitedBy: Int? = null,
)

// CollectionInviteResponseEnvelope removed. Use CollectionInviteResponse directly.

@Serializable
data class CollectionInviteResponse(
    @SerialName("token") val token: String,
    @SerialName("role") val role: String,
    @SerialName("expires_at") val expiresAt: String? = null,
)
