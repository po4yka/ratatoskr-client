package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.TagRepository
import org.koin.core.annotation.Factory

@Factory
class MergeTagsUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(sourceTagIds: List<Int>, targetTagId: Int) =
        tagRepository.mergeTags(sourceTagIds = sourceTagIds, targetTagId = targetTagId)
}
