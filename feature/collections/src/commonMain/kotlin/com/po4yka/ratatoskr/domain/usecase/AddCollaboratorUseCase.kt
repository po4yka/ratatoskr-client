package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class AddCollaboratorUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        userId: Int,
        role: CollaboratorRole,
    ) {
        repository.addCollaborator(collectionId, userId, role)
    }
}
