package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.AttachTagsRequestDto
import com.po4yka.bitesizereader.data.remote.dto.CreateTagRequestDto
import com.po4yka.bitesizereader.data.remote.dto.MergeTagsRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TagDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TagDetachResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TagDto
import com.po4yka.bitesizereader.data.remote.dto.TagListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TagMergeResponseDto
import com.po4yka.bitesizereader.data.remote.dto.UpdateTagRequestDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [TagsApi::class])
class KtorTagsApi(private val client: HttpClient) : TagsApi {
    override suspend fun listTags(): ApiResponseDto<TagListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/tags").body()
        }

    override suspend fun createTag(request: CreateTagRequestDto): ApiResponseDto<TagDto> {
        return client.post("v1/tags") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getTag(tagId: Int): ApiResponseDto<TagDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/tags/$tagId").body()
        }

    override suspend fun updateTag(
        tagId: Int,
        request: UpdateTagRequestDto,
    ): ApiResponseDto<TagDto> {
        return client.patch("v1/tags/$tagId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteTag(tagId: Int): ApiResponseDto<TagDeleteResponseDto> {
        return client.delete("v1/tags/$tagId").body()
    }

    override suspend fun mergeTags(request: MergeTagsRequestDto): ApiResponseDto<TagMergeResponseDto> {
        return client.post("v1/tags/merge") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun attachTags(
        summaryId: Long,
        request: AttachTagsRequestDto,
    ): ApiResponseDto<TagListResponseDto> {
        return client.post("v1/summaries/$summaryId/tags") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun detachTag(
        summaryId: Long,
        tagId: Int,
    ): ApiResponseDto<TagDetachResponseDto> {
        return client.delete("v1/summaries/$summaryId/tags/$tagId").body()
    }
}
