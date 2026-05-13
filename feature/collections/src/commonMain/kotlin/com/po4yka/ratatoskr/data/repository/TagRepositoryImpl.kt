package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.api.TagsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdTagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsMergeRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsTagIdRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.TagDto
import com.po4yka.ratatoskr.data.remote.dto.TagListResponseDto
import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import kotlinx.serialization.json.JsonElement
import org.koin.core.annotation.Single

@Single(binds = [TagRepository::class])
class TagRepositoryImpl : TagRepository {
    override suspend fun listTags(): List<Tag> {
        val envelope = TagsApi.listTagsV1TagsGet().unwrap().decodeEnvelope<TagListResponseDto>()
        return envelope?.tags?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun createTag(
        name: String,
        color: String?,
    ): Tag {
        val tag =
            TagsApi.createTagV1TagsPost(
                body = V1TagsRequest(name = name, color = color),
            ).unwrap().decodeEnvelope<TagDto>()
        return requireNotNull(tag) { "Server returned no data for tag creation" }.toDomain()
    }

    override suspend fun getTag(tagId: Int): Tag {
        val tag =
            TagsApi.getTagV1TagsTagIdGet(tagId = tagId.toLong())
                .unwrap()
                .decodeEnvelope<TagDto>()
        return requireNotNull(tag) { "Tag $tagId not found" }.toDomain()
    }

    override suspend fun updateTag(
        tagId: Int,
        name: String?,
        color: String?,
    ): Tag {
        val tag =
            TagsApi.updateTagV1TagsTagIdPatch(
                tagId = tagId.toLong(),
                body = V1TagsTagIdRequest(name = name, color = color),
            ).unwrap().decodeEnvelope<TagDto>()
        return requireNotNull(tag) { "Server returned no data for tag update" }.toDomain()
    }

    override suspend fun deleteTag(tagId: Int) {
        TagsApi.deleteTagV1TagsTagIdDelete(tagId = tagId.toLong()).unwrap()
    }

    override suspend fun mergeTags(
        sourceTagIds: List<Int>,
        targetTagId: Int,
    ) {
        TagsApi.mergeTagsV1TagsMergePost(
            body =
                V1TagsMergeRequest(
                    sourceTagIds = sourceTagIds.map { it.toLong() },
                    targetTagId = targetTagId.toLong(),
                ),
        ).unwrap()
    }

    override suspend fun attachTags(
        summaryId: String,
        tagIds: List<Int>?,
        tagNames: List<String>?,
    ): List<Tag> {
        val longSummaryId =
            summaryId.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid summary ID: $summaryId")
        val envelope =
            TagsApi.attachTagsV1SummariesSummaryIdTagsPost(
                summaryId = longSummaryId,
                body =
                    V1SummariesSummaryIdTagsRequest(
                        tagIds = tagIds?.map { it.toLong() },
                        tagNames = tagNames,
                    ),
            ).unwrap().decodeEnvelope<TagListResponseDto>()
        return envelope?.tags?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun detachTag(
        summaryId: String,
        tagId: Int,
    ) {
        val longSummaryId =
            summaryId.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid summary ID: $summaryId")
        TagsApi.detachTagV1SummariesSummaryIdTagsTagIdDelete(
            summaryId = longSummaryId,
            tagId = tagId.toLong(),
        ).unwrap()
    }
}

/**
 * Decodes the `data` field of a `{success, meta, data}` envelope returned
 * by the JsonElement-typed generated endpoint. Returns null when no `data`
 * field is present.
 */
private inline fun <reified T> JsonElement.decodeEnvelope(): T? {
    val obj = (this as? kotlinx.serialization.json.JsonObject) ?: return null
    val data = obj["data"] ?: return null
    if (data is kotlinx.serialization.json.JsonNull) return null
    return Api.json.decodeFromJsonElement(
        kotlinx.serialization.serializer<T>(),
        data,
    )
}
