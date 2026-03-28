package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.CollectionsApi
import com.po4yka.bitesizereader.data.remote.dto.CollectionCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionItemCreateRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionShareRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionUpdateRequest
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.CollectionRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [CollectionRepository::class])
class CollectionRepositoryImpl(
    private val api: CollectionsApi,
    private val database: Database,
) : CollectionRepository {
    override fun getCollections(): Flow<List<Collection>> =
        flow {
            val response = api.listCollections()
            if (response.success && response.data != null) {
                val collections = requireNotNull(response.data).collections.map { it.toDomain() }
                emit(collections)
            } else {
                throw Exception("Failed to fetch collections: ${response.error}")
            }
        }

    override suspend fun getCollection(id: String): Collection? {
        val intId = id.toIntOrNull() ?: return null
        val response = api.getCollection(intId)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw Exception("Failed to get collection $id: ${response.error}")
        }
    }

    override suspend fun getCollectionItems(
        collectionId: String,
        limit: Int,
        offset: Int,
    ): List<Summary> {
        val intId =
            collectionId.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid collection ID: $collectionId")
        val response = api.listItems(intId, limit, offset)
        if (response.success && response.data != null) {
            val items = requireNotNull(response.data).items
            // CollectionItem only contains summary_id. Look up full summary data
            // from the local database (offline-first). Items not yet synced locally
            // are skipped with a warning.
            return items.mapNotNull { item ->
                val summaryId = item.summaryId.toString()
                val entity = database.databaseQueries.getSummaryById(summaryId).executeAsOneOrNull()
                if (entity != null) {
                    entity.toDomain()
                } else {
                    logger.warn { "Summary $summaryId not found in local DB, skipping" }
                    null
                }
            }
        } else {
            throw Exception("Failed to get collection items for $collectionId: ${response.error}")
        }
    }

    override suspend fun updateCollection(
        id: String,
        name: String?,
        description: String?,
    ): Collection {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val request =
            CollectionUpdateRequest(
                name = name,
                description = description,
            )
        val response = api.updateCollection(intId, request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw Exception("Failed to update collection: ${response.error}")
        }
    }

    override suspend fun deleteCollection(id: String) {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response = api.deleteCollection(intId)
        if (!response.success) {
            throw Exception("Failed to delete collection: ${response.error}")
        }
    }

    override suspend fun getCollectionAcl(id: String): List<CollectionAcl> {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response = api.getAcl(intId)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).acl.map { it.toDomain() }
        } else {
            throw Exception("Failed to get ACL for collection $id: ${response.error}")
        }
    }

    override suspend fun addCollaborator(
        id: String,
        userId: Int,
        role: CollaboratorRole,
    ) {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val request =
            CollectionShareRequest(
                userId = userId,
                role = role.toApiString(),
            )
        val response = api.addCollaborator(intId, request)
        if (!response.success) {
            throw Exception("Failed to add collaborator: ${response.error}")
        }
    }

    override suspend fun removeCollaborator(
        id: String,
        userId: Int,
    ) {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response = api.removeCollaborator(intId, userId)
        if (!response.success) {
            throw Exception("Failed to remove collaborator: ${response.error}")
        }
    }

    override suspend fun createInviteLink(
        id: String,
        role: CollaboratorRole,
        expiresAt: String?,
    ): CollectionInvite {
        val intId = id.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val request =
            CollectionInviteRequest(
                role = role.toApiString(),
                expiresAt = expiresAt,
            )
        val response = api.createInvite(intId, request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw Exception("Failed to create invite link: ${response.error}")
        }
    }

    override suspend fun addToCollection(
        collectionId: String,
        summaryId: String,
    ) {
        val intId =
            collectionId.toIntOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $collectionId")
        val intSummaryId =
            summaryId.toIntOrNull() ?: throw IllegalArgumentException("Invalid summary ID: $summaryId")
        val response =
            api.addItem(
                id = intId,
                request = CollectionItemCreateRequest(summaryId = intSummaryId),
            )
        if (!response.success) {
            throw Exception("Failed to add item to collection: ${response.error}")
        }
    }

    override suspend fun createCollection(
        name: String,
        description: String?,
    ): Collection {
        val request = CollectionCreateRequest(name = name, description = description)
        val response = api.createCollection(request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw Exception("Failed to create collection: ${response.error}")
        }
    }
}
