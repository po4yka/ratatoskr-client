package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Tag

interface TagRepository {
    suspend fun listTags(): List<Tag>

    suspend fun createTag(
        name: String,
        color: String? = null,
    ): Tag

    suspend fun getTag(tagId: Int): Tag

    suspend fun updateTag(
        tagId: Int,
        name: String? = null,
        color: String? = null,
    ): Tag

    suspend fun deleteTag(tagId: Int)

    suspend fun mergeTags(
        sourceTagIds: List<Int>,
        targetTagId: Int,
    )

    suspend fun attachTags(
        summaryId: Long,
        tagIds: List<Int>? = null,
        tagNames: List<String>? = null,
    ): List<Tag>

    suspend fun detachTag(
        summaryId: Long,
        tagId: Int,
    )
}
