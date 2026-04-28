package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class CreateCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        name: String,
        description: String? = null,
    ): Collection {
        return repository.createCollection(name = name, description = description)
    }
}
