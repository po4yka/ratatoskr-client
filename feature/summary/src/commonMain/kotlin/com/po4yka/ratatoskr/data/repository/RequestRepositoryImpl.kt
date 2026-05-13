package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.RequestsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.ForwardMetadata
import com.po4yka.ratatoskr.api.generated.models.SubmitForwardRequest
import com.po4yka.ratatoskr.api.generated.models.SubmitRequestResponse
import com.po4yka.ratatoskr.api.generated.models.SubmitURLRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.mappers.toEntity
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.repository.RequestRepository
import com.po4yka.ratatoskr.util.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.time.Clock
import org.koin.core.annotation.Single

@Single(binds = [RequestRepository::class])
class RequestRepositoryImpl(
    private val database: Database,
) : RequestRepository {
    override suspend fun submitUrl(url: String): Request {
        val envelope = RequestsApi.submitRequestV1RequestsPost(
            body = SubmitURLRequest(inputUrl = url),
        ).unwrap()
        val requestData = envelope.data
            ?: throw AppError.ServerError(code = 500, fallbackMessage = "Failed to submit request")
        val requestEntity = requestData.request.toEntity(url)
        database.databaseQueries.insertRequest(requestEntity)
        return requestData.request.toDomain(url)
    }

    override suspend fun submitForward(
        contentText: String,
        langPreference: String,
    ): Request {
        // Spec gap: ForwardMetadata is non-nullable in the generated model but not provided
        // by the caller. Using placeholder values for required Telegram metadata fields.
        val envelope = RequestsApi.submitRequestV1RequestsPost(
            body = SubmitForwardRequest(
                contentText = contentText,
                forwardMetadata = ForwardMetadata(fromChatId = 0L, fromMessageId = 0L),
            ),
        ).unwrap()
        val requestData = envelope.data
            ?: throw AppError.ServerError(code = 500, fallbackMessage = "Failed to submit forward request")
        val requestEntity = requestData.request.toEntity("forward:text")
        database.databaseQueries.insertRequest(requestEntity)
        return requestData.request.toDomain("forward:text")
    }

    override suspend fun getRequestStatus(id: String): Request {
        val requestId =
            id.toLongOrNull()
                ?: throw IllegalArgumentException("Request id must be numeric to query status")
        val envelope = RequestsApi.getRequestStatusV1RequestsRequestIdStatusGet(requestId).unwrap()
        val statusData = envelope.data
            ?: throw AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch request status")

        val existing =
            database.databaseQueries.selectAllRequests()
                .executeAsList()
                .find { it.id == id }
                ?: throw NoSuchElementException("Request not found locally")

        val updatedEntity =
            existing.copy(
                status = statusData.status,
                updatedAt = Clock.System.now(),
            )
        database.databaseQueries.insertRequest(updatedEntity)
        return updatedEntity.toDomain()
    }

    override fun getRequests(): Flow<List<Request>> {
        return database.databaseQueries.selectAllRequests()
            .asFlow().mapToList(Dispatchers.IO).map { entities ->
                entities.map { it.toDomain() }
            }
    }
}
