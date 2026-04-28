package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(
        tagId: Int,
        name: String? = null,
        color: String? = null,
    ): Tag = tagRepository.updateTag(tagId = tagId, name = name, color = color)
}
