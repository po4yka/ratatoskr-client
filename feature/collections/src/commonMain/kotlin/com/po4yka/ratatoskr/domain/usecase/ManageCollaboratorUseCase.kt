package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class ManageCollaboratorUseCase(private val repository: CollectionRepository) {
    suspend fun addCollaborator(
        collectionId: String,
        userId: Int,
        role: CollaboratorRole,
    ) {
        repository.addCollaborator(collectionId, userId, role)
    }

    suspend fun removeCollaborator(
        collectionId: String,
        userId: Int,
    ) {
        repository.removeCollaborator(collectionId, userId)
    }
}
