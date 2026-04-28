package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.mappers.toEntity
import com.po4yka.ratatoskr.data.remote.RequestsApi
import com.po4yka.ratatoskr.data.remote.dto.SubmitForwardRequestDto
import com.po4yka.ratatoskr.data.remote.dto.SubmitURLRequestDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.repository.RequestRepository
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
    private val api: RequestsApi,
) : RequestRepository {
    override suspend fun submitUrl(url: String): Request {
        val request = SubmitURLRequestDto(inputUrl = url)
        val response = api.submitUrl(request)
        val requestDto = response.data ?: throw IllegalStateException("Failed to submit request")
        val requestEntity = requestDto.toEntity(url)
        database.databaseQueries.insertRequest(requestEntity)
        return requestDto.toDomain(url)
    }

    override suspend fun submitForward(
        contentText: String,
        langPreference: String,
    ): Request {
        val request = SubmitForwardRequestDto(contentText = contentText, langPreference = langPreference)
        val response = api.submitForward(request)
        val requestDto = response.data ?: throw IllegalStateException("Failed to submit forward request")
        val requestEntity = requestDto.toEntity("forward:text")
        database.databaseQueries.insertRequest(requestEntity)
        return requestDto.toDomain("forward:text")
    }

    override suspend fun getRequestStatus(id: String): Request {
        val requestId =
            id.toLongOrNull()
                ?: throw IllegalArgumentException("Request id must be numeric to query status")
        val response = api.getRequestStatus(requestId)
        val statusDto = response.data ?: throw IllegalStateException("Failed to fetch request status")
        val existing =
            database.databaseQueries.selectAllRequests()
                .executeAsList()
                .find { it.id == id }
                ?: throw NoSuchElementException("Request not found locally")

        val updatedEntity =
            existing.copy(
                status = statusDto.status,
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
