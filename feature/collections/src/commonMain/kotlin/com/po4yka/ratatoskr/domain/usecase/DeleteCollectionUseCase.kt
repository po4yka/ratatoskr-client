package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteCollection(id)
    }
}
