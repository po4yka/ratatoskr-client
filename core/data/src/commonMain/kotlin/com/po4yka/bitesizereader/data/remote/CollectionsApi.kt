package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CollectionAclResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionDto
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemMoveRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemReorderRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemsMoveResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemsResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionListResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionMoveRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionMoveResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionReorderRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionShareRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionTreeResponse
import com.po4yka.bitesizereader.data.remote.dto.CollectionUpdateRequest
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse

interface CollectionsApi {
    suspend fun listCollections(): ApiResponseDto<CollectionListResponse>

    suspend fun createCollection(request: CollectionCreateRequest): ApiResponseDto<CollectionDto>

    suspend fun getCollection(id: Int): ApiResponseDto<CollectionDto>

    suspend fun updateCollection(
        id: Int,
        request: CollectionUpdateRequest,
    ): ApiResponseDto<CollectionDto>

    suspend fun deleteCollection(id: Int): ApiResponseDto<SuccessResponse>

    suspend fun addItem(
        id: Int,
        request: CollectionItemCreateRequest,
    ): ApiResponseDto<SuccessResponse>

    suspend fun listItems(
        id: Int,
        limit: Int = 50,
        offset: Int = 0,
    ): ApiResponseDto<CollectionItemsResponse>

    suspend fun removeItem(
        id: Int,
        summaryId: Long,
    ): ApiResponseDto<SuccessResponse>

    suspend fun getTree(maxDepth: Int = 3): ApiResponseDto<CollectionTreeResponse>

    // Note: CollectionAclResponseEnvelope not defined in DTOs yet, will need to check or assume generic
    suspend fun getAcl(id: Int): ApiResponseDto<CollectionAclResponse>

    suspend fun addCollaborator(
        id: Int,
        request: CollectionShareRequest,
    ): ApiResponseDto<SuccessResponse>

    suspend fun removeCollaborator(
        id: Int,
        userId: Int,
    ): ApiResponseDto<SuccessResponse>

    suspend fun createInvite(
        id: Int,
        request: CollectionInviteRequest,
    ): ApiResponseDto<CollectionInviteResponse>

    suspend fun acceptInvite(token: String): ApiResponseDto<SuccessResponse>

    suspend fun reorderCollections(
        id: Int,
        request: CollectionReorderRequest,
    ): ApiResponseDto<SuccessResponse>

    // Assuming same response type based on similarity
    suspend fun reorderItems(
        id: Int,
        request: CollectionItemReorderRequest,
    ): ApiResponseDto<SuccessResponse>

    suspend fun moveCollection(
        id: Int,
        request: CollectionMoveRequest,
    ): ApiResponseDto<CollectionMoveResponse>

    suspend fun moveItems(
        id: Int,
        request: CollectionItemMoveRequest,
    ): ApiResponseDto<CollectionItemsMoveResponse>
}
