package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Tag
import com.po4yka.bitesizereader.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class CreateTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(name: String, color: String? = null): Tag =
        tagRepository.createTag(name = name, color = color)
}
