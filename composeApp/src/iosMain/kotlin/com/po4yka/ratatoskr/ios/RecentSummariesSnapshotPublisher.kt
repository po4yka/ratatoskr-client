package com.po4yka.ratatoskr.ios

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.usecase.GetSummariesUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults
import kotlin.time.Clock

private val logger = KotlinLogging.logger {}

private object AppGroupContract {
    const val APP_GROUP_ID = "group.com.po4yka.ratatoskr"
    const val RECENT_SUMMARIES_SNAPSHOT_KEY = "recentSummariesSnapshot"
    const val RECENT_SUMMARIES_SNAPSHOT_TIMESTAMP_KEY = "recentSummariesSnapshotTimestamp"
}

@Serializable
private data class RecentSummariesSnapshot(
    val generatedAtEpochSeconds: Long,
    val summaries: List<RecentSummarySnapshotItem>,
)

@Serializable
private data class RecentSummarySnapshotItem(
    val id: String,
    val title: String,
    val excerpt: String,
    val domain: String? = null,
    val readingTimeMinutes: Int? = null,
)

class RecentSummariesSnapshotPublisher(
    private val getSummariesUseCase: GetSummariesUseCase,
) {
    suspend fun publish(limit: Int = 3): Int {
        val pageSize = limit.coerceAtLeast(1)
        val summaries =
            getSummariesUseCase(page = 1, pageSize = pageSize)
                .first()
                .sortedByDescending { it.createdAt }
                .take(pageSize)

        val snapshot =
            RecentSummariesSnapshot(
                generatedAtEpochSeconds = Clock.System.now().epochSeconds,
                summaries = summaries.map(Summary::toSnapshotItem),
            )

        val snapshotJson = Json.encodeToString(snapshot)
        val sharedDefaults = NSUserDefaults(suiteName = AppGroupContract.APP_GROUP_ID)
        if (sharedDefaults == null) {
            logger.warn { "Unable to access app group defaults for widget snapshot publishing" }
            return 0
        }

        sharedDefaults.setObject(snapshotJson, forKey = AppGroupContract.RECENT_SUMMARIES_SNAPSHOT_KEY)
        sharedDefaults.setDouble(
            Clock.System.now().epochSeconds.toDouble(),
            forKey = AppGroupContract.RECENT_SUMMARIES_SNAPSHOT_TIMESTAMP_KEY,
        )
        sharedDefaults.synchronize()

        logger.info { "Published ${snapshot.summaries.size} recent summaries to app group" }
        return snapshot.summaries.size
    }
}

private fun Summary.toSnapshotItem(): RecentSummarySnapshotItem =
    RecentSummarySnapshotItem(
        id = id,
        title = title,
        excerpt = (fullContent?.takeIf { it.isNotBlank() } ?: content).toExcerpt(),
        domain = sourceUrl.toDomainOrNull(),
        readingTimeMinutes = readingTimeMin,
    )

private fun String.toExcerpt(maxLength: Int = 140): String {
    val normalized = trim().replace(Regex("\\s+"), " ")
    if (normalized.length <= maxLength) return normalized
    return normalized.take(maxLength).trimEnd() + "..."
}

private fun String.toDomainOrNull(): String? {
    val host = substringAfter("://", this).substringBefore("/").removePrefix("www.").trim()
    return host.takeIf { it.isNotBlank() }
}
