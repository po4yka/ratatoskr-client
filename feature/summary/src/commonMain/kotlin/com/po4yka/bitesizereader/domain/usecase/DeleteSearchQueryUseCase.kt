package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SearchRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteSearchQueryUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(query: String) {
        repository.deleteSearchQuery(query)
    }
}
