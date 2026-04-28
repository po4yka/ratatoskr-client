package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.TagsApi
import com.po4yka.ratatoskr.data.remote.dto.AttachTagsRequestDto
import com.po4yka.ratatoskr.data.remote.dto.CreateTagRequestDto
import com.po4yka.ratatoskr.data.remote.dto.MergeTagsRequestDto
import com.po4yka.ratatoskr.data.remote.dto.UpdateTagRequestDto
import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Single

@Single(binds = [TagRepository::class])
class TagRepositoryImpl(
    private val tagsApi: TagsApi,
) : TagRepository {
    override suspend fun listTags(): List<Tag> {
        val response = tagsApi.listTags()
        return response.data?.tags?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun createTag(
        name: String,
        color: String?,
    ): Tag {
        val response = tagsApi.createTag(CreateTagRequestDto(name = name, color = color))
        return requireNotNull(response.data) { "Server returned no data for tag creation" }.toDomain()
    }

    override suspend fun getTag(tagId: Int): Tag {
        val response = tagsApi.getTag(tagId)
        return requireNotNull(response.data) { "Tag $tagId not found" }.toDomain()
    }

    override suspend fun updateTag(
        tagId: Int,
        name: String?,
        color: String?,
    ): Tag {
        val response = tagsApi.updateTag(tagId, UpdateTagRequestDto(name = name, color = color))
        return requireNotNull(response.data) { "Server returned no data for tag update" }.toDomain()
    }

    override suspend fun deleteTag(tagId: Int) {
        tagsApi.deleteTag(tagId)
    }

    override suspend fun mergeTags(
        sourceTagIds: List<Int>,
        targetTagId: Int,
    ) {
        tagsApi.mergeTags(MergeTagsRequestDto(sourceTagIds = sourceTagIds, targetTagId = targetTagId))
    }

    override suspend fun attachTags(
        summaryId: Long,
        tagIds: List<Int>?,
        tagNames: List<String>?,
    ): List<Tag> {
        val response =
            tagsApi.attachTags(
                summaryId = summaryId,
                request = AttachTagsRequestDto(tagIds = tagIds, tagNames = tagNames),
            )
        return response.data?.tags?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun detachTag(
        summaryId: Long,
        tagId: Int,
    ) {
        tagsApi.detachTag(summaryId = summaryId, tagId = tagId)
    }
}
