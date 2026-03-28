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
    @SerialName("createdAt") val createdAt: String,
    @SerialName("description") val description: String? = null,
    @SerialName("parentId") val parentId: Int? = null,
    @SerialName("position") val position: Int? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("serverVersion") val serverVersion: Int? = null,
    @SerialName("isShared") val isShared: Boolean = false,
    @SerialName("shareCount") val shareCount: Int? = null,
    @SerialName("itemCount") val itemCount: Int? = null,
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
    @SerialName("collectionId") val collectionId: Int,
    @SerialName("summaryId") val summaryId: Int,
    @SerialName("createdAt") val createdAt: String,
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
    @SerialName("pagination") val pagination: PaginationDto? = null,
)

@Serializable
data class CollectionTreeResponse(
    @SerialName("collections") val collections: List<CollectionDto>,
)

// CollectionReorderResponseEnvelope removed. Use SuccessResponse or specific data.

// CollectionMoveResponseEnvelope removed. Use CollectionMoveResponse directly.

@Serializable
data class CollectionMoveResponse(
    @SerialName("id") val id: Int,
    @SerialName("parentId") val parentId: Int? = null,
    @SerialName("position") val position: Int,
    @SerialName("updatedAt") val updatedAt: String? = null,
)

// CollectionItemsMoveResponseEnvelope removed. Use CollectionItemsMoveResponse directly.

@Serializable
data class CollectionItemsMoveResponse(
    @SerialName("movedSummaryIds") val movedSummaryIds: List<Long>,
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
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("invitedBy") val invitedBy: Int? = null,
)

// CollectionInviteResponseEnvelope removed. Use CollectionInviteResponse directly.

@Serializable
data class CollectionInviteResponse(
    @SerialName("token") val token: String,
    @SerialName("role") val role: String,
    @SerialName("expiresAt") val expiresAt: String? = null,
)
