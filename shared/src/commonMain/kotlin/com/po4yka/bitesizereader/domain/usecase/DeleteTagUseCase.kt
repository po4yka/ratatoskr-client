package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(tagId: Int) = tagRepository.deleteTag(tagId)
}
