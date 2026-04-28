package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class GetCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String): Collection? {
        return repository.getCollection(id)
    }
}
