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
import kotlinx.datetime.Clock

class RequestRepositoryImpl(
    private val database: Database,
    private val api: RequestsApi
) : RequestRepository {

    override suspend fun submitUrl(url: String): Request {
        val requestDto = api.submitUrl(url)
        val requestEntity = requestDto.toEntity()
        database.databaseQueries.insertRequest(requestEntity)
        return requestDto.toDomain()
    }

    override suspend fun getRequestStatus(id: String): Request {
        val statusDto = api.getRequestStatus(id)
        val existing = database.databaseQueries.selectAllRequests()
            .executeAsList()
            .find { it.id == id }
            ?: throw Exception("Request not found locally")

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
