package com.po4yka.ratatoskr.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.ratatoskr.api.generated.api.DigestsApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1DigestsCustomRequest
import com.po4yka.ratatoskr.data.remote.dto.CustomDigestResponseDto
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.CustomDigest
import com.po4yka.ratatoskr.domain.model.CustomDigestStatus
import com.po4yka.ratatoskr.domain.model.DigestFormat
import com.po4yka.ratatoskr.domain.repository.CustomDigestRepository
import com.po4yka.ratatoskr.util.error.AppError
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

private const val DIGEST_LIST_LIMIT = 50L
private const val POLL_INTERVAL_SECONDS = 5L
private const val MAX_POLL_ITERATIONS = 60

private val customDigestJson = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalUuidApi::class)
@Single(binds = [CustomDigestRepository::class])
class CustomDigestRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CustomDigestRepository {
    override suspend fun createDigest(
        title: String,
        summaryIds: List<String>,
        format: DigestFormat,
    ): CustomDigest =
        withContext(ioDispatcher) {
            val localId = Uuid.random().toString()
            val now = Clock.System.now()
            val formatStr = format.name.lowercase()

            try {
                database.databaseQueries.insertCustomDigest(
                    id = localId,
                    title = title,
                    summaryIds = summaryIds.joinToString(","),
                    format = formatStr,
                    content = null,
                    status = CustomDigestStatus.PENDING.name.lowercase(),
                    createdAt = now,
                )

                val envelope =
                    DigestsApi.createCustomDigestV1DigestsCustomPost(
                        V1DigestsCustomRequest(
                            summaryIds = summaryIds,
                            format = formatStr,
                            title = title,
                        ),
                    ).unwrap()

                val dto = decodeCustomDigestData(envelope)
                    ?: run {
                        database.databaseQueries.deleteCustomDigest(localId)
                        throw AppError.UnknownError(fallbackMessage = "Failed to create digest")
                    }

                val serverCreatedAt = parseInstant(dto.createdAt, now)
                val serverStatus = dto.status
                database.databaseQueries.insertCustomDigest(
                    id = dto.id,
                    title = dto.title,
                    summaryIds = summaryIds.joinToString(","),
                    format = formatStr,
                    content = dto.content,
                    status = serverStatus,
                    createdAt = serverCreatedAt,
                )
                // Remove local placeholder if server assigned a different id
                if (dto.id != localId) {
                    database.databaseQueries.deleteCustomDigest(localId)
                }
                CustomDigest(
                    id = dto.id,
                    title = dto.title,
                    summaryIds = summaryIds,
                    format = format,
                    content = dto.content,
                    status = parseStatus(dto.status),
                    createdAt = serverCreatedAt,
                )
            } catch (e: Exception) {
                runCatching { database.databaseQueries.deleteCustomDigest(localId) }
                throw e
            }
        }

    override suspend fun getDigest(id: String): CustomDigest? =
        withContext(ioDispatcher) {
            database.databaseQueries.getCustomDigestById(id).executeAsOneOrNull()?.toDomain()
        }

    override fun getDigests(): Flow<List<CustomDigest>> =
        database.databaseQueries.getAllCustomDigests(DIGEST_LIST_LIMIT)
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun pollDigestStatus(id: String): CustomDigest =
        withContext(ioDispatcher) {
            var iterations = 0
            while (iterations < MAX_POLL_ITERATIONS) {
                iterations++
                delay(POLL_INTERVAL_SECONDS.seconds)
                val envelope = DigestsApi.getCustomDigestV1DigestsCustomDigestIdGet(id).unwrap()
                val dto = decodeCustomDigestData(envelope) ?: continue
                val status = parseStatus(dto.status)
                database.databaseQueries.updateCustomDigestContent(
                    content = dto.content,
                    status = dto.status,
                    id = id,
                )
                if (status == CustomDigestStatus.COMPLETED || status == CustomDigestStatus.FAILED) {
                    return@withContext database.databaseQueries.getCustomDigestById(id)
                        .executeAsOneOrNull()
                        ?.toDomain()
                        ?: CustomDigest(
                            id = id,
                            title = dto.title,
                            summaryIds = emptyList(),
                            format = DigestFormat.BRIEF,
                            content = dto.content,
                            status = status,
                            createdAt = Clock.System.now(),
                        )
                }
            }
            // Timed out: mark as failed locally
            database.databaseQueries.updateCustomDigestContent(content = null, status = "failed", id = id)
            throw AppError.UnknownError(fallbackMessage = "Digest generation timed out")
        }

    override suspend fun deleteDigest(id: String): Unit =
        withContext(ioDispatcher) {
            database.databaseQueries.deleteCustomDigest(id)
            Unit
        }

    private fun com.po4yka.ratatoskr.database.CustomDigestEntity.toDomain(): CustomDigest =
        CustomDigest(
            id = id,
            title = title,
            summaryIds = summaryIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            format = parseFormat(format),
            content = content,
            status = parseStatus(status),
            createdAt = createdAt,
        )

    private fun parseFormat(value: String): DigestFormat =
        when (value.uppercase()) {
            "DETAILED" -> DigestFormat.DETAILED
            else -> DigestFormat.BRIEF
        }

    private fun parseStatus(value: String): CustomDigestStatus =
        when (value.uppercase()) {
            "GENERATING" -> CustomDigestStatus.GENERATING
            "COMPLETED" -> CustomDigestStatus.COMPLETED
            "FAILED" -> CustomDigestStatus.FAILED
            else -> CustomDigestStatus.PENDING
        }

    private fun parseInstant(
        value: String,
        fallback: Instant,
    ): Instant =
        try {
            Instant.parse(value)
        } catch (_: Exception) {
            fallback
        }
}

private fun decodeCustomDigestData(envelope: JsonElement): CustomDigestResponseDto? {
    val dataField = (envelope as? JsonObject)?.get("data")
        ?.takeIf { it !is JsonNull }
        ?: return null
    return customDigestJson.decodeFromJsonElement(CustomDigestResponseDto.serializer(), dataField)
}
