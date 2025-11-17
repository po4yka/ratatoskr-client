@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.bitesizereader.data.local.DatabaseHelper
import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.mappers.toSubmitURLRequestDto
import com.po4yka.bitesizereader.data.remote.api.RequestsApi
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of RequestRepository with status polling
 */
class RequestRepositoryImpl(
    private val requestsApi: RequestsApi,
    private val database: Database,
    private val databaseHelper: DatabaseHelper,
) : RequestRepository {
    override suspend fun submitURL(
        url: String,
        langPreference: String,
    ): Result<Request> {
        return try {
            val requestDto = url.toSubmitURLRequestDto(langPreference)
            val response = requestsApi.submitURL(requestDto)

            if (response.success && response.data != null) {
                val request = response.data.toDomain().copy(inputUrl = url)
                databaseHelper.insertRequest(request)
                Result.success(request)
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to submit URL"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRequestStatus(requestId: Int): Result<Request> {
        return try {
            val response = requestsApi.getRequestStatus(requestId)

            if (response.success && response.data != null) {
                val request = response.data.toDomain()
                databaseHelper.insertRequest(request)
                Result.success(request)
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to get status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun pollRequestStatus(requestId: Int): Flow<Request> =
        flow {
            var isCompleted = false

            while (!isCompleted) {
                val result = getRequestStatus(requestId)

                result.onSuccess { request ->
                    emit(request)

                    // Stop polling if completed, error, or cancelled
                    if (request.status in
                        listOf(
                            RequestStatus.COMPLETED,
                            RequestStatus.ERROR,
                            RequestStatus.CANCELLED,
                        )
                    ) {
                        isCompleted = true
                    }
                }.onFailure {
                    // On error, emit cached data if available
                    val cached =
                        database.requestQueries.selectById(requestId.toLong())
                            .executeAsOneOrNull()

                    if (cached != null) {
                        emit(mapDbRequestToDomain(cached))
                    }
                    isCompleted = true
                }

                if (!isCompleted) {
                    delay(3000) // Poll every 3 seconds
                }
            }
        }

    override suspend fun retryRequest(requestId: Int): Result<Request> {
        return try {
            val response = requestsApi.retryRequest(requestId)

            if (response.success && response.data != null) {
                val request = response.data.toDomain()
                databaseHelper.insertRequest(request)
                Result.success(request)
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to retry"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelRequest(requestId: Int): Result<Request> {
        return try {
            val response = requestsApi.cancelRequest(requestId)

            if (response.success && response.data != null) {
                val request = response.data.toDomain()
                databaseHelper.insertRequest(request)
                Result.success(request)
            } else {
                Result.failure(Exception(response.error?.message ?: "Failed to cancel"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllRequests(): Flow<List<Request>> {
        return database.requestQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { mapDbRequestToDomain(it) }
            }
    }

    override fun getPendingRequests(): Flow<List<Request>> {
        return database.requestQueries.selectPending()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { mapDbRequestToDomain(it) }
            }
    }

    override suspend fun deleteRequest(requestId: Int): Result<Unit> {
        return try {
            database.requestQueries.deleteById(requestId.toLong())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapDbRequestToDomain(dbRequest: com.po4yka.bitesizereader.database.Request): Request {
        return Request(
            id = dbRequest.id.toInt(),
            inputUrl = dbRequest.inputUrl,
            type = com.po4yka.bitesizereader.domain.model.RequestType.valueOf(dbRequest.type),
            status = RequestStatus.valueOf(dbRequest.status),
            stage =
                dbRequest.stage?.let {
                    com.po4yka.bitesizereader.domain.model.ProcessingStage.valueOf(it)
                },
            progress = dbRequest.progress.toInt(),
            langPreference = dbRequest.langPreference,
            summaryId = dbRequest.summaryId?.toInt(),
            errorMessage = dbRequest.errorMessage,
            canRetry = dbRequest.canRetry == 1L,
            estimatedSecondsRemaining = dbRequest.estimatedSecondsRemaining?.toInt(),
            createdAt = kotlinx.datetime.Instant.parse(dbRequest.createdAt),
            updatedAt = dbRequest.updatedAt?.let { kotlinx.datetime.Instant.parse(it) },
            completedAt = dbRequest.completedAt?.let { kotlinx.datetime.Instant.parse(it) },
        )
    }
}
