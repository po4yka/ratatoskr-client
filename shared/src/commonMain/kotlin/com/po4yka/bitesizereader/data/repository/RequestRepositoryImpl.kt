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

class RequestRepositoryImpl(
    private val database: Database,
    private val api: RequestsApi
) : RequestRepository {

    override suspend fun submitUrl(url: String): Request {
        val requestDto = api.submitUrl(url)
        val requestEntity = requestDto.toEntity()
        database.databaseQueries.requestEntityQueries.insertRequest(requestEntity)
        return requestDto.toDomain()
    }

    override suspend fun getRequestStatus(id: String): Request {
        val statusDto = api.getRequestStatus(id)
        // We only get status, need to update local entity
        val currentEntity = database.databaseQueries.requestEntityQueries.selectAllRequests()
            .executeAsList().find { it.id == id }
            
        if (currentEntity != null) {
             // Update logic would go here if we had status in Entity matching DTO
             // database.requestEntityQueries.updateRequestStatus(...)
        }
        // Returning mock or incomplete request based on statusDto if needed
        // For now, just return what's in DB
         return database.databaseQueries.requestEntityQueries.selectAllRequests()
            .executeAsList().find { it.id == id }?.toDomain() 
            ?: throw Exception("Request not found locally")
    }

    override fun getRequests(): Flow<List<Request>> {
        return database.databaseQueries.requestEntityQueries.selectAllRequests()
            .asFlow().mapToList(Dispatchers.IO).map { entities ->
                entities.map { it.toDomain() }
            }
    }
}