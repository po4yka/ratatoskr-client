package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        id: String,
        name: String? = null,
        description: String? = null,
    ): Collection {
        return repository.updateCollection(id, name, description)
    }
}
