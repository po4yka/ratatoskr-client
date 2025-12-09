package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.model.Summary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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

class MockCollectionRepository : CollectionRepository {
    private val mockCollections =
        listOf(
            // System Collections
            Collection("unsorted", "Unsorted", 9, "inbox", CollectionType.System),
            Collection("read_later", "Read Later", 22, "bookmark", CollectionType.System),
            // User Collections (Group 1 - Work/Projects)
            Collection(
                id = "beautiful_web",
                name = "Beautiful Web",
                count = 25,
                iconName = "palette",
                type = CollectionType.User,
                description = "Collection of beautiful web designs",
            ),
            Collection("inspiration", "Inspiration Board", 79, "lightbulb", CollectionType.User),
            Collection("vacation", "Vacation Ideas", 16, "map", CollectionType.User),
            Collection("food", "Food", 6, "restaurant", CollectionType.User),
            Collection("games", "Games", 0, "sports_esports", CollectionType.User),
            // User Collections (Group 2 - Other)
            Collection("wishboard", "Wishboard", 13, "spa", CollectionType.User),
            Collection("design", "Design", 79, "diamond", CollectionType.User),
            Collection("flat_design", "Flat Design", 10, "architecture", CollectionType.User),
            // Trash
            Collection("trash", "Trash", 0, "delete", CollectionType.System),
        )

    override fun getCollections(): Flow<List<Collection>> {
        return flowOf(mockCollections)
    }

    override suspend fun getCollection(id: String): Collection? {
        return mockCollections.find { it.id == id }
    }

    override suspend fun getCollectionItems(
        collectionId: String,
        limit: Int,
        offset: Int,
    ): List<Summary> {
        // Return empty list for mock - real implementation will fetch from API
        return emptyList()
    }

    override suspend fun updateCollection(
        id: String,
        name: String?,
        description: String?,
        isPublic: Boolean?,
    ): Collection {
        val existing = mockCollections.find { it.id == id } ?: throw IllegalArgumentException("Collection not found")
        return existing.copy(
            name = name ?: existing.name,
            description = description ?: existing.description,
            isPublic = isPublic ?: existing.isPublic,
        )
    }

    override suspend fun deleteCollection(id: String) {
        // No-op for mock
    }

    override suspend fun getCollectionAcl(id: String): List<CollectionAcl> {
        // Return mock ACL with just the owner
        return listOf(
            CollectionAcl(
                userId = 1,
                role = CollaboratorRole.Owner,
                status = "active",
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z",
            ),
        )
    }

    override suspend fun addCollaborator(
        id: String,
        userId: Int,
        role: CollaboratorRole,
    ) {
        // No-op for mock
    }

    override suspend fun removeCollaborator(
        id: String,
        userId: Int,
    ) {
        // No-op for mock
    }

    override suspend fun createInviteLink(
        id: String,
        role: CollaboratorRole,
        expiresAt: String?,
    ): CollectionInvite {
        return CollectionInvite(
            token = "mock-invite-token-${id}",
            role = role,
            expiresAt = expiresAt,
        )
    }
}
