package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CollectionAclResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemMoveRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemReorderRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemsMoveResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemsResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionListResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionMoveRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionMoveResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionReorderRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionReorderResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionShareRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionTreeResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.CollectionUpdateRequest
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse

interface CollectionsApi {
    suspend fun listCollections(): ApiResponseDto<CollectionListResponseEnvelope>

    suspend fun createCollection(request: CollectionCreateRequest): ApiResponseDto<CollectionResponseEnvelope>

    suspend fun getCollection(id: Int): ApiResponseDto<CollectionResponseEnvelope>

    suspend fun updateCollection(
        id: Int,
        request: CollectionUpdateRequest,
    ): ApiResponseDto<CollectionResponseEnvelope>

    suspend fun deleteCollection(id: Int): ApiResponseDto<SuccessResponse>

    suspend fun addItem(
        id: Int,
        request: CollectionItemCreateRequest,
    ): ApiResponseDto<SuccessResponse>

    suspend fun listItems(
        id: Int,
        limit: Int = 50,
        offset: Int = 0,
    ): ApiResponseDto<CollectionItemsResponseEnvelope>

    suspend fun removeItem(
        id: Int,
        summaryId: Long,
    ): ApiResponseDto<SuccessResponse>

    suspend fun getTree(maxDepth: Int = 3): ApiResponseDto<CollectionTreeResponseEnvelope>

    // Note: CollectionAclResponseEnvelope not defined in DTOs yet, will need to check or assume generic
    suspend fun getAcl(id: Int): ApiResponseDto<CollectionAclResponseEnvelope>

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
    ): ApiResponseDto<CollectionInviteResponseEnvelope>

    suspend fun acceptInvite(token: String): ApiResponseDto<SuccessResponse>

    suspend fun reorderCollections(
        id: Int,
        request: CollectionReorderRequest,
    ): ApiResponseDto<CollectionReorderResponseEnvelope>

    // Assuming same response type based on similarity
    suspend fun reorderItems(
        id: Int,
        request: CollectionItemReorderRequest,
    ): ApiResponseDto<CollectionReorderResponseEnvelope>

    suspend fun moveCollection(
        id: Int,
        request: CollectionMoveRequest,
    ): ApiResponseDto<CollectionMoveResponseEnvelope>

    suspend fun moveItems(
        id: Int,
        request: CollectionItemMoveRequest,
    ): ApiResponseDto<CollectionItemsMoveResponseEnvelope>
}
