package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(tagId: Int) = tagRepository.deleteTag(tagId)
}
