package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteCollection(id)
    }
}
