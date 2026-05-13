package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.CollectionsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.CollectionCreateRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionInviteRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionItemCreateRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionShareRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionUpdateRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.Collection
import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.domain.model.CollectionAcl
import com.po4yka.ratatoskr.domain.model.CollectionInvite
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.feature.collections.domain.repository.CollectionRepository
import com.po4yka.ratatoskr.util.error.AppError
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [CollectionRepository::class])
class CollectionRepositoryImpl(
    private val database: Database,
) : CollectionRepository {
    override fun getCollections(): Flow<List<Collection>> =
        flow {
            val response = CollectionsApi.getCollections().unwrap()
            val collections = response.data?.collections.orEmpty().map { it.toDomain() }
            emit(collections)
        }

    override suspend fun getCollection(id: String): Collection {
        val longId =
            id.toLongOrNull()
                ?: throw AppError.UnknownError(fallbackMessage = "Invalid collection ID: $id")
        val response = CollectionsApi.getCollection(longId).unwrap()
        return response.data?.toDomain()
            ?: throw AppError.UnknownError(fallbackMessage = "Failed to get collection $id")
    }

    override suspend fun getCollectionItems(
        collectionId: String,
        limit: Int,
        offset: Int,
    ): List<Summary> {
        val longId =
            collectionId.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid collection ID: $collectionId")
        val response =
            CollectionsApi.listCollectionItems(
                collectionId = longId,
                limit = limit.toLong(),
                offset = offset.toLong(),
            ).unwrap()
        val items = response.data?.items.orEmpty()
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
    }

    override suspend fun updateCollection(
        id: String,
        name: String?,
        description: String?,
    ): Collection {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response =
            CollectionsApi.updateCollection(
                collectionId = longId,
                body = CollectionUpdateRequest(name = name, description = description),
            ).unwrap()
        return response.data?.toDomain()
            ?: throw AppError.UnknownError(fallbackMessage = "Failed to update collection")
    }

    override suspend fun deleteCollection(id: String) {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        CollectionsApi.deleteCollection(longId).unwrap()
    }

    override suspend fun getCollectionAcl(id: String): List<CollectionAcl> {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response = CollectionsApi.getCollectionAcl(longId).unwrap()
        return response.data?.acl.orEmpty().map { it.toDomain() }
    }

    override suspend fun addCollaborator(
        id: String,
        userId: Int,
        role: CollaboratorRole,
    ) {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        CollectionsApi.addCollectionCollaborator(
            collectionId = longId,
            body =
                CollectionShareRequest(
                    userId = userId.toLong(),
                    role = role.toShareRole(),
                ),
        ).unwrap()
    }

    override suspend fun removeCollaborator(
        id: String,
        userId: Int,
    ) {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        CollectionsApi.removeCollectionCollaborator(
            collectionId = longId,
            targetUserId = userId.toLong(),
        ).unwrap()
    }

    override suspend fun createInviteLink(
        id: String,
        role: CollaboratorRole,
        expiresAt: String?,
    ): CollectionInvite {
        val longId =
            id.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $id")
        val response =
            CollectionsApi.createCollectionInvite(
                collectionId = longId,
                body =
                    CollectionInviteRequest(
                        role = role.toInviteRole(),
                        expiresAt = expiresAt?.let { kotlin.time.Instant.parse(it) },
                    ),
            ).unwrap()
        return response.data?.toDomain()
            ?: throw AppError.UnknownError(fallbackMessage = "Failed to create invite link")
    }

    override suspend fun addToCollection(
        collectionId: String,
        summaryId: String,
    ) {
        val longId =
            collectionId.toLongOrNull() ?: throw IllegalArgumentException("Invalid collection ID: $collectionId")
        val longSummaryId =
            summaryId.toLongOrNull() ?: throw IllegalArgumentException("Invalid summary ID: $summaryId")
        CollectionsApi.addCollectionItem(
            collectionId = longId,
            body = CollectionItemCreateRequest(summaryId = longSummaryId),
        ).unwrap()
    }

    override suspend fun createCollection(
        name: String,
        description: String?,
    ): Collection {
        val response =
            CollectionsApi.createCollection(
                body = CollectionCreateRequest(name = name, description = description),
            ).unwrap()
        return response.data?.toDomain()
            ?: throw AppError.UnknownError(fallbackMessage = "Failed to create collection")
    }
}

private fun CollaboratorRole.toShareRole(): CollectionShareRequest.Role =
    when (this) {
        CollaboratorRole.Editor -> CollectionShareRequest.Role.EDITOR
        CollaboratorRole.Viewer -> CollectionShareRequest.Role.VIEWER
        CollaboratorRole.Owner ->
            throw IllegalArgumentException("Cannot share a collection with the owner role")
    }

private fun CollaboratorRole.toInviteRole(): CollectionInviteRequest.Role =
    when (this) {
        CollaboratorRole.Editor -> CollectionInviteRequest.Role.EDITOR
        CollaboratorRole.Viewer -> CollectionInviteRequest.Role.VIEWER
        CollaboratorRole.Owner ->
            throw IllegalArgumentException("Cannot invite with the owner role")
    }
