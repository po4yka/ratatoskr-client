package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Tag
import com.po4yka.bitesizereader.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class GetTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(): List<Tag> = tagRepository.listTags()
}
