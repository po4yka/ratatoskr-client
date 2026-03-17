package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.bitesizereader.data.remote.CustomDigestApi
import com.po4yka.bitesizereader.data.remote.dto.CreateCustomDigestRequestDto
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.CustomDigest
import com.po4yka.bitesizereader.domain.model.CustomDigestStatus
import com.po4yka.bitesizereader.domain.model.DigestFormat
import com.po4yka.bitesizereader.domain.repository.CustomDigestRepository
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
import org.koin.core.annotation.Single

private const val DIGEST_LIST_LIMIT = 50L
private const val POLL_INTERVAL_SECONDS = 5L

@OptIn(ExperimentalUuidApi::class)
@Single(binds = [CustomDigestRepository::class])
class CustomDigestRepositoryImpl(
    private val database: Database,
    private val api: CustomDigestApi,
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

            database.databaseQueries.insertCustomDigest(
                id = localId,
                title = title,
                summaryIds = summaryIds.joinToString(","),
                format = formatStr,
                content = null,
                status = CustomDigestStatus.PENDING.name.lowercase(),
                createdAt = now,
            )

            val response =
                api.createCustomDigest(
                    CreateCustomDigestRequestDto(
                        summaryIds = summaryIds,
                        format = formatStr,
                        title = title,
                    ),
                )

            val dto = response.data
            if (dto != null) {
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
            } else {
                CustomDigest(
                    id = localId,
                    title = title,
                    summaryIds = summaryIds,
                    format = format,
                    content = null,
                    status = CustomDigestStatus.PENDING,
                    createdAt = now,
                )
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
            while (true) {
                delay(POLL_INTERVAL_SECONDS.seconds)
                val response = api.getCustomDigest(id)
                val dto = response.data ?: continue
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
            @Suppress("UNREACHABLE_CODE")
            error("Unreachable")
        }

    override suspend fun deleteDigest(id: String): Unit =
        withContext(ioDispatcher) {
            database.databaseQueries.deleteCustomDigest(id)
            Unit
        }

    private fun com.po4yka.bitesizereader.database.CustomDigestEntity.toDomain(): CustomDigest =
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
