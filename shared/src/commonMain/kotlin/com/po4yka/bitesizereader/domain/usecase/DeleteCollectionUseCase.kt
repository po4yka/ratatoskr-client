package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.CollectionRepository

class DeleteCollectionUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteCollection(id)
    }
}
