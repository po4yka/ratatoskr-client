package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.repository.CollectionRepository

class CreateInviteLinkUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(
        collectionId: String,
        role: CollaboratorRole,
        expiresAt: String? = null,
    ): CollectionInvite {
        return repository.createInviteLink(collectionId, role, expiresAt)
    }
}
