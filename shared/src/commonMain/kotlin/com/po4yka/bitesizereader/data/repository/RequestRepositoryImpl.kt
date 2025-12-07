package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.mappers.toEntity
import com.po4yka.bitesizereader.data.remote.RequestsApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.time.Clock

class RequestRepositoryImpl(
    private val database: Database,
    private val api: RequestsApi
) : RequestRepository {

    override suspend fun submitUrl(url: String): Request {
        val response = api.submitUrl(url)
        val requestDto = response.data ?: throw IllegalStateException("Failed to submit request")
        val requestEntity = requestDto.toEntity(url)
        database.databaseQueries.insertRequest(requestEntity)
        return requestDto.toDomain(url)
    }

    override suspend fun getRequestStatus(id: String): Request {
        val response = api.getRequestStatus(id)
        val statusDto = response.data ?: throw IllegalStateException("Failed to fetch request status")
        val existing = database.databaseQueries.selectAllRequests()
            .executeAsList()
            .find { it.id == id }
            ?: throw NoSuchElementException("Request not found locally")

        val updatedEntity = existing.copy(
            status = statusDto.status,
            updatedAt = Clock.System.now()
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
