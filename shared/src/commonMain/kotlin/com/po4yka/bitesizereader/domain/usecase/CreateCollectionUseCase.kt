package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
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
