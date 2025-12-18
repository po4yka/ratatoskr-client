package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class GetAvailableTagsUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(): List<String> = repository.getAllTags()
}
