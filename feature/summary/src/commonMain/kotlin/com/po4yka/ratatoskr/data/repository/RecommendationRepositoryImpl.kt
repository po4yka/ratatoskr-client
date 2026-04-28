package com.po4yka.ratatoskr.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.Recommendation
import com.po4yka.ratatoskr.domain.model.RecommendationStrategy
import com.po4yka.ratatoskr.domain.model.SortOrder
import com.po4yka.ratatoskr.domain.repository.RecommendationRepository
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

private const val DEFAULT_RECOMMENDATION_LIMIT = 10
private const val RECENCY_BONUS = 0.5
private const val FAVORITED_BONUS = 0.3
private const val BASE_SCORE = 1.0

@Single(binds = [RecommendationRepository::class])
class RecommendationRepositoryImpl(
    private val database: Database,
    private val summaryRepository: SummaryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RecommendationRepository {
    override fun getRecommendations(limit: Int): Flow<List<Recommendation>> {
        val recommendationFlow =
            database.databaseQueries
                .getRecommendations(limit = limit.toLong())
                .asFlow()
                .mapToList(ioDispatcher)

        val summariesFlow =
            summaryRepository.getSummaries(
                page = 1,
                pageSize = Int.MAX_VALUE / 2,
                tags = null,
            )

        return combine(recommendationFlow, summariesFlow) { entities, summaries ->
            val summaryMap = summaries.associateBy { it.id }
            entities.mapNotNull { entity ->
                val summary =
                    summaryMap[entity.summaryId] ?: run {
                        logger.warn { "No summary found for rec ${entity.id}, summaryId=${entity.summaryId}" }
                        return@mapNotNull null
                    }
                Recommendation(
                    id = entity.id,
                    summary = summary,
                    score = entity.score,
                    reason = entity.reason,
                    strategy =
                        RecommendationStrategy.entries.find { s -> s.value == entity.strategy }
                            ?: RecommendationStrategy.TRENDING,
                )
            }
        }
    }

    override suspend fun refreshRecommendations() {
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            val staleThreshold = now - 6.hours
            database.databaseQueries.clearStaleRecommendations(staleThreshold)

            val unreadSummaries =
                summaryRepository.getSummariesFiltered(
                    page = 1,
                    pageSize = 100,
                    readFilter = ReadFilter.UNREAD,
                    sortOrder = SortOrder.NEWEST,
                    selectedTag = null,
                ).first()

            val recentCutoff = now - 7.days

            val scored =
                unreadSummaries.map { summary ->
                    var score = BASE_SCORE
                    if (summary.createdAt >= recentCutoff) score += RECENCY_BONUS
                    if (summary.isFavorited) score += FAVORITED_BONUS
                    summary to score
                }.sortedByDescending { it.second }.take(DEFAULT_RECOMMENDATION_LIMIT)

            for ((summary, score) in scored) {
                val recId = "rec_${summary.id}_${now.toEpochMilliseconds()}"
                val reason =
                    when {
                        summary.createdAt >= recentCutoff -> "Recently added"
                        else -> "Unread in your library"
                    }
                database.databaseQueries.insertRecommendation(
                    id = recId,
                    summaryId = summary.id,
                    score = score,
                    reason = reason,
                    strategy = RecommendationStrategy.TRENDING.value,
                    fetchedAt = now,
                )
            }

            logger.debug { "Refreshed ${scored.size} recommendations" }
        }
    }

    override suspend fun dismissRecommendation(id: String) {
        withContext(ioDispatcher) {
            database.databaseQueries.deleteRecommendation(id)
        }
    }
}
