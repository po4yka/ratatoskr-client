package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class GetCollectionAclUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(collectionId: String): List<CollectionAcl> {
        return repository.getCollectionAcl(collectionId)
    }
}
