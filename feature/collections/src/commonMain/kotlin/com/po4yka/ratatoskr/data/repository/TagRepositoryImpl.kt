package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.TagsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdTagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsMergeRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsTagIdRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Single

@Single(binds = [TagRepository::class])
class TagRepositoryImpl : TagRepository {
    override suspend fun listTags(): List<Tag> {
        val data = TagsApi.listTagsV1TagsGet().unwrap().data
        return data?.tags?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun createTag(
        name: String,
        color: String?,
    ): Tag {
        val tag =
            TagsApi.createTagV1TagsPost(
                body = V1TagsRequest(name = name, color = color),
            ).unwrap().data
        return requireNotNull(tag) { "Server returned no data for tag creation" }.toDomain()
    }

    override suspend fun getTag(tagId: Int): Tag {
        val tag = TagsApi.getTagV1TagsTagIdGet(tagId = tagId.toLong()).unwrap().data
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
            ).unwrap().data
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
        val data =
            TagsApi.attachTagsV1SummariesSummaryIdTagsPost(
                summaryId = longSummaryId,
                body =
                    V1SummariesSummaryIdTagsRequest(
                        tagIds = tagIds?.map { it.toLong() },
                        tagNames = tagNames,
                    ),
            ).unwrap().data
        return data?.tags?.map { it.toDomain() } ?: emptyList()
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
