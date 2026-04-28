package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.AttachTagsRequestDto
import com.po4yka.ratatoskr.data.remote.dto.CreateTagRequestDto
import com.po4yka.ratatoskr.data.remote.dto.MergeTagsRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TagDeleteResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TagDetachResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TagDto
import com.po4yka.ratatoskr.data.remote.dto.TagListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TagMergeResponseDto
import com.po4yka.ratatoskr.data.remote.dto.UpdateTagRequestDto

interface TagsApi {
    suspend fun listTags(): ApiResponseDto<TagListResponseDto>

    suspend fun createTag(request: CreateTagRequestDto): ApiResponseDto<TagDto>

    suspend fun getTag(tagId: Int): ApiResponseDto<TagDto>

    suspend fun updateTag(
        tagId: Int,
        request: UpdateTagRequestDto,
    ): ApiResponseDto<TagDto>

    suspend fun deleteTag(tagId: Int): ApiResponseDto<TagDeleteResponseDto>

    suspend fun mergeTags(request: MergeTagsRequestDto): ApiResponseDto<TagMergeResponseDto>

    suspend fun attachTags(
        summaryId: Long,
        request: AttachTagsRequestDto,
    ): ApiResponseDto<TagListResponseDto>

    suspend fun detachTag(
        summaryId: Long,
        tagId: Int,
    ): ApiResponseDto<TagDetachResponseDto>
}
