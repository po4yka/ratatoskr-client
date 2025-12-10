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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single
class KtorCollectionsApi(private val client: HttpClient) : CollectionsApi {
    override suspend fun listCollections(): ApiResponseDto<CollectionListResponse> {
        return client.get("v1/collections").body()
    }

    override suspend fun createCollection(
        request: CollectionCreateRequest,
    ): ApiResponseDto<CollectionDto> {
        return client.post("v1/collections") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getCollection(id: Int): ApiResponseDto<CollectionDto> {
        return client.get("v1/collections/$id").body()
    }

    override suspend fun updateCollection(
        id: Int,
        request: CollectionUpdateRequest,
    ): ApiResponseDto<CollectionDto> {
        return client.patch("v1/collections/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteCollection(id: Int): ApiResponseDto<SuccessResponse> {
        return client.delete("v1/collections/$id").body()
    }

    override suspend fun addItem(
        id: Int,
        request: CollectionItemCreateRequest,
    ): ApiResponseDto<SuccessResponse> {
        return client.post("v1/collections/$id/items") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun listItems(
        id: Int,
        limit: Int,
        offset: Int,
    ): ApiResponseDto<CollectionItemsResponse> {
        return client.get("v1/collections/$id/items") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body()
    }

    override suspend fun removeItem(
        id: Int,
        summaryId: Long,
    ): ApiResponseDto<SuccessResponse> {
        return client.delete("v1/collections/$id/items/$summaryId").body()
    }

    override suspend fun getTree(maxDepth: Int): ApiResponseDto<CollectionTreeResponse> {
        return client.get("v1/collections/tree") {
            parameter("max_depth", maxDepth)
        }.body()
    }

    override suspend fun getAcl(id: Int): ApiResponseDto<CollectionAclResponse> {
        return client.get("v1/collections/$id/acl").body()
    }

    override suspend fun addCollaborator(
        id: Int,
        request: CollectionShareRequest,
    ): ApiResponseDto<SuccessResponse> {
        return client.post("v1/collections/$id/share") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun removeCollaborator(
        id: Int,
        userId: Int,
    ): ApiResponseDto<SuccessResponse> {
        return client.delete("v1/collections/$id/share/$userId").body()
    }

    override suspend fun createInvite(
        id: Int,
        request: CollectionInviteRequest,
    ): ApiResponseDto<CollectionInviteResponse> {
        return client.post("v1/collections/$id/invite") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun acceptInvite(token: String): ApiResponseDto<SuccessResponse> {
        return client.post("v1/collections/invites/$token/accept").body()
    }

    override suspend fun reorderCollections(
        id: Int,
        request: CollectionReorderRequest,
    ): ApiResponseDto<SuccessResponse> {
        return client.post("v1/collections/$id/reorder") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun reorderItems(
        id: Int,
        request: CollectionItemReorderRequest,
    ): ApiResponseDto<SuccessResponse> {
        return client.post("v1/collections/$id/items/reorder") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun moveCollection(
        id: Int,
        request: CollectionMoveRequest,
    ): ApiResponseDto<CollectionMoveResponse> {
        return client.post("v1/collections/$id/move") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun moveItems(
        id: Int,
        request: CollectionItemMoveRequest,
    ): ApiResponseDto<CollectionItemsMoveResponse> {
        return client.post("v1/collections/$id/items/move") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
