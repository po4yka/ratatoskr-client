package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.CollectionsApi
import com.po4yka.bitesizereader.data.remote.dto.CollectionInviteRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionShareRequest
import com.po4yka.bitesizereader.data.remote.dto.CollectionUpdateRequest
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
) : CollectionRepository {
    override fun getCollections(): Flow<List<Collection>> =
        flow {
            try {
                val response = api.listCollections()
                if (response.success && response.data != null) {
                    val collections = response.data.collections.map { it.toDomain() }
                    emit(collections)
                } else {
                    logger.error { "Failed to fetch collections: ${response.error}" }
                    emit(emptyList())
                }
            } catch (e: Exception) {
                logger.error(e) { "Error fetching collections" }
                emit(emptyList())
            }
        }

    override suspend fun getCollection(id: String): Collection? {
        return try {
            val intId = id.toIntOrNull() ?: return null
            val response = api.getCollection(intId)
            if (response.success && response.data != null) {
                response.data.toDomain()
            } else {
                logger.error { "Failed to get collection $id: ${response.error}" }
                null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error getting collection $id" }
            null
        }
    }

    override suspend fun getCollectionItems(
        collectionId: String,
        limit: Int,
        offset: Int,
    ): List<Summary> {
        return try {
            val intId = collectionId.toIntOrNull() ?: return emptyList()
            val response = api.listItems(intId, limit, offset)
            if (response.success && response.data != null) {
                // CollectionItem only has summary_id; return minimal stubs.
                // Full summary data should be fetched separately.
                response.data.items.map { item ->
                    Summary(
                        id = item.summaryId.toString(),
                        title = "Summary #${item.summaryId}",
                        content = "",
                        sourceUrl = "",
                        imageUrl = null,
                        createdAt =
                            runCatching {
                                kotlin.time.Instant.parse(item.createdAt)
                            }.getOrElse { kotlin.time.Clock.System.now() },
                        isRead = false,
                        tags = emptyList(),
                    )
                }
            } else {
                logger.error { "Failed to get collection items for $collectionId: ${response.error}" }
                emptyList()
            }
        } catch (e: Exception) {
            logger.error(e) { "Error getting collection items for $collectionId" }
            emptyList()
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
            return response.data.toDomain()
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
        return try {
            val intId = id.toIntOrNull() ?: return emptyList()
            val response = api.getAcl(intId)
            if (response.success && response.data != null) {
                response.data.acl.map { it.toDomain() }
            } else {
                logger.error { "Failed to get ACL for collection $id: ${response.error}" }
                emptyList()
            }
        } catch (e: Exception) {
            logger.error(e) { "Error getting ACL for collection $id" }
            emptyList()
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
            return response.data.toDomain()
        } else {
            throw Exception("Failed to create invite link: ${response.error}")
        }
    }
}
