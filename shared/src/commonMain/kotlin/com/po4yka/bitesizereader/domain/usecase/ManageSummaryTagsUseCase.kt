package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Tag
import com.po4yka.bitesizereader.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class ManageSummaryTagsUseCase(private val tagRepository: TagRepository) {
    suspend fun attach(
        summaryId: Long,
        tagIds: List<Int>? = null,
        tagNames: List<String>? = null,
    ): List<Tag> = tagRepository.attachTags(summaryId = summaryId, tagIds = tagIds, tagNames = tagNames)

    suspend fun detach(summaryId: Long, tagId: Int) =
        tagRepository.detachTag(summaryId = summaryId, tagId = tagId)
}
