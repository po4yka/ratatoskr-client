package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollections(): Flow<List<Collection>>

    suspend fun getCollection(id: String): Collection?

    suspend fun getCollectionItems(
        collectionId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): List<Summary>

    suspend fun updateCollection(
        id: String,
        name: String? = null,
        description: String? = null,
        isPublic: Boolean? = null,
    ): Collection

    suspend fun deleteCollection(id: String)

    suspend fun getCollectionAcl(id: String): List<CollectionAcl>

    suspend fun addCollaborator(
        id: String,
        userId: Int,
        role: CollaboratorRole,
    )

    suspend fun removeCollaborator(
        id: String,
        userId: Int,
    )

    suspend fun createInviteLink(
        id: String,
        role: CollaboratorRole,
        expiresAt: String? = null,
    ): CollectionInvite
}
