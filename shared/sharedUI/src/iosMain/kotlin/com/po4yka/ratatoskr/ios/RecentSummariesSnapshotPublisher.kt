package com.po4yka.ratatoskr.ios

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.usecase.GetSummariesUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSData
import platform.Foundation.NSDataWritingFileProtectionCompleteUntilFirstUserAuthentication
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL
import kotlin.time.Clock

private val logger = KotlinLogging.logger {}

private object AppGroupContract {
    const val APP_GROUP_ID = "group.com.po4yka.ratatoskr"
    const val SNAPSHOT_FILE_NAME = "recent-summaries-snapshot.json"
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
    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
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

        val containerUrl =
            NSFileManager.defaultManager
                .containerURLForSecurityApplicationGroupIdentifier(AppGroupContract.APP_GROUP_ID)
        if (containerUrl == null) {
            logger.warn { "App group container unavailable for widget snapshot publishing" }
            return 0
        }
        val snapshotUrl: NSURL =
            containerUrl.URLByAppendingPathComponent(AppGroupContract.SNAPSHOT_FILE_NAME)
                ?: run {
                    logger.warn { "Unable to compose snapshot URL inside app group container" }
                    return 0
                }

        val jsonData: NSData? =
            NSString.create(string = snapshotJson).dataUsingEncoding(NSUTF8StringEncoding)
        if (jsonData == null) {
            logger.warn { "Snapshot JSON could not be UTF-8 encoded for app group write" }
            return 0
        }

        val wrote: Boolean =
            memScoped {
                val errorVar = alloc<ObjCObjectVar<NSError?>>()
                val ok =
                    jsonData.writeToURL(
                        snapshotUrl,
                        options = NSDataWritingFileProtectionCompleteUntilFirstUserAuthentication,
                        error = errorVar.ptr,
                    )
                if (!ok) {
                    logger.warn { "Snapshot write failed: ${errorVar.value?.localizedDescription}" }
                }
                ok
            }
        if (!wrote) return 0

        // Exclude from iCloud / iTunes backup. Failure is non-fatal — the
        // snapshot is regenerated on every publish.
        memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            val excluded =
                snapshotUrl.setResourceValue(
                    NSNumber(bool = true),
                    forKey = NSURLIsExcludedFromBackupKey,
                    error = errorVar.ptr,
                )
            if (!excluded) {
                logger.warn { "Failed to set isExcludedFromBackupKey: ${errorVar.value?.localizedDescription}" }
            }
        }

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
