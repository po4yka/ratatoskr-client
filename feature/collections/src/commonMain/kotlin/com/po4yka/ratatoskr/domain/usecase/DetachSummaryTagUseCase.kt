package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class DetachSummaryTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(
        summaryId: Long,
        tagId: Int,
    ) = tagRepository.detachTag(summaryId = summaryId, tagId = tagId)
}
