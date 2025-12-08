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
    @SerialName("children") val children: List<CollectionDto> = emptyList()
)

@Serializable
data class CollectionTreeNodeDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("children") val children: List<CollectionTreeNodeDto> = emptyList()
)

@Serializable
data class CollectionCreateRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean = false,
    @SerialName("parent_id") val parentId: Int? = null
)

@Serializable
data class CollectionUpdateRequest(
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("is_public") val isPublic: Boolean? = null,
    @SerialName("parent_id") val parentId: Int? = null
)

@Serializable
data class CollectionItemCreateRequest(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("notes") val notes: String? = null
)

@Serializable
data class CollectionItemDto(
    @SerialName("collection_id") val collectionId: Int,
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("added_at") val addedAt: String,
    @SerialName("notes") val notes: String? = null,
    @SerialName("summary") val summary: SummaryDetailDto? = null
)

@Serializable
data class CollectionShareRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("role") val role: String // "editor" or "viewer"
)

@Serializable
data class CollectionInviteRequest(
    @SerialName("role") val role: String,
    @SerialName("expires_at") val expiresAt: String? = null
)

@Serializable
data class CollectionReorderRequest(
    @SerialName("items") val items: List<CollectionReorderItem>
)

@Serializable
data class CollectionReorderItem(
    @SerialName("collection_id") val collectionId: Int,
    @SerialName("position") val position: Int
)

@Serializable
data class CollectionItemReorderRequest(
    @SerialName("items") val items: List<CollectionItemReorderItem>
)

@Serializable
data class CollectionItemReorderItem(
    @SerialName("summary_id") val summaryId: Long,
    @SerialName("position") val position: Int
)

@Serializable
data class CollectionMoveRequest(
    @SerialName("parent_id") val parentId: Int?,
    @SerialName("position") val position: Int? = null
)

@Serializable
data class CollectionItemMoveRequest(
    @SerialName("summary_ids") val summaryIds: List<Long>,
    @SerialName("target_collection_id") val targetCollectionId: Int,
    @SerialName("position") val position: Int? = null
)

@Serializable
data class CollectionListResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<CollectionDto>
)

@Serializable
data class CollectionResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: CollectionDto
)

@Serializable
data class CollectionItemsResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<CollectionItemDto>,
    @SerialName("meta") val meta: MetaDto? = null
)

@Serializable
data class CollectionTreeResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: List<CollectionTreeNodeDto>
)

@Serializable
data class SuccessResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null
)

@Serializable
data class CollectionReorderResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: SuccessResponse? = null // Assuming nested success or minimal data
)

@Serializable
data class CollectionMoveResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: CollectionMoveResponse
)

@Serializable
data class CollectionMoveResponse(
    @SerialName("id") val id: Int,
    @SerialName("parent_id") val parentId: Int? = null,
    @SerialName("position") val position: Int,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class CollectionItemsMoveResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: CollectionItemsMoveResponse
)

@Serializable
data class CollectionItemsMoveResponse(
    @SerialName("moved_summary_ids") val movedSummaryIds: List<Long>
)

@Serializable
data class CollectionAclResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: CollectionAclResponse
)

@Serializable
data class CollectionAclResponse(
    @SerialName("acl") val acl: List<CollectionAclEntry>
)

@Serializable
data class CollectionAclEntry(
    @SerialName("user_id") val userId: Int,
    @SerialName("role") val role: String,
    @SerialName("status") val status: String,
    @SerialName("invited_by") val invitedBy: Int? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class CollectionInviteResponseEnvelope(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: CollectionInviteResponse
)

@Serializable
data class CollectionInviteResponse(
    @SerialName("token") val token: String,
    @SerialName("role") val role: String,
    @SerialName("expires_at") val expiresAt: String? = null
)
