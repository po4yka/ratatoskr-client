package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class GetCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String): Collection? {
        return repository.getCollection(id)
    }
}
