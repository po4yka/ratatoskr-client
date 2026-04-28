package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.domain.model.CollectionInvite
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import org.koin.core.annotation.Factory

@Factory
class CreateInviteLinkUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        role: CollaboratorRole,
        expiresAt: String? = null,
    ): CollectionInvite {
        return repository.createInviteLink(collectionId, role, expiresAt)
    }
}
