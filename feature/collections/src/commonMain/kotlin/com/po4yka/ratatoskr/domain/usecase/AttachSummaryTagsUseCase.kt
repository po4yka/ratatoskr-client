package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class AttachSummaryTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(
        summaryId: String,
        tagIds: List<Int>? = null,
        tagNames: List<String>? = null,
    ): List<Tag> = tagRepository.attachTags(summaryId = summaryId, tagIds = tagIds, tagNames = tagNames)
}
