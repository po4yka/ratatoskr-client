package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Tag
import com.po4yka.ratatoskr.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class GetTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(): List<Tag> = tagRepository.listTags()
}
