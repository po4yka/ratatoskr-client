package com.po4yka.ratatoskr.feature.sync.domain.health

import kotlin.time.Instant

/**
 * Live snapshot of the sync subsystem rendered by the Settings → Help →
 * Diagnostics → Sync health debug screen.
 *
 * The debug surface displays one row per applier with last-run timestamp,
 * success/fail status, retry count, and the last error. The "last error"
 * column intentionally never carries a raw [Throwable.message] —
 * sync errors regularly embed bearer tokens (Ktor exception messages quote
 * full request URLs), user emails (server validation errors echo the
 * request body), or article excerpts (LLM pipeline errors quote the input).
 * Per spec: "use category + correlation id, not raw payloads."
 *
 * [categorizeError] is the only sanctioned path from a raw failure to a
 * displayable [SyncErrorSummary]. It drops the raw message, keeps the
 * caller-supplied correlation id (trimmed, with a stable sentinel for
 * blank inputs), and tags the failure with a coarse [SyncErrorCategory]
 * the caller picks based on where in the sync graph the exception bubbled
 * up.
 */
enum class SyncErrorCategory {
    NETWORK,
    SERVER,
    PARSE,
    CONFLICT,
    STORAGE,
    AUTH,
    UNKNOWN,
}

enum class SyncErrorCategoryHint {
    NETWORK,
    SERVER,
    PARSE,
    CONFLICT,
    STORAGE,
    AUTH,
}

data class SyncErrorSummary(
    val category: SyncErrorCategory,
    val correlationId: String,
)

data class SyncApplierRow(
    val name: String,
    val lastRunAt: Instant?,
    val lastError: SyncErrorSummary?,
    val retryCount: Int,
    val successCount: Int,
) {
    fun render(): String {
        val statusToken =
            if (lastError == null) {
                "OK"
            } else {
                "${lastError.category} (${lastError.correlationId})"
            }
        return "$name | $statusToken | retries=$retryCount"
    }
}

data class SyncHealthSnapshot(
    val lastSyncAt: Instant?,
    val appliers: List<SyncApplierRow>,
    val pendingOperationsDepth: Int,
) {
    val hasAnyFailure: Boolean get() = appliers.any { it.lastError != null }

    companion object {
        const val MAX_CORRELATION_ID_LENGTH: Int = 36
        const val MISSING_CORRELATION_ID: String = "-"

        fun empty(): SyncHealthSnapshot =
            SyncHealthSnapshot(lastSyncAt = null, appliers = emptyList(), pendingOperationsDepth = 0)

        fun categorizeError(
            raw: Throwable,
            correlationId: String?,
            hint: SyncErrorCategoryHint?,
        ): SyncErrorSummary {
            @Suppress("UNUSED_VARIABLE")
            val ignored = raw
            return SyncErrorSummary(
                category = hint?.toCategory() ?: SyncErrorCategory.UNKNOWN,
                correlationId = sanitizeCorrelationId(correlationId),
            )
        }

        private fun sanitizeCorrelationId(id: String?): String {
            val trimmed = id?.trim().orEmpty()
            if (trimmed.isEmpty()) return MISSING_CORRELATION_ID
            return if (trimmed.length > MAX_CORRELATION_ID_LENGTH) {
                trimmed.substring(0, MAX_CORRELATION_ID_LENGTH)
            } else {
                trimmed
            }
        }

        private fun SyncErrorCategoryHint.toCategory(): SyncErrorCategory =
            when (this) {
                SyncErrorCategoryHint.NETWORK -> SyncErrorCategory.NETWORK
                SyncErrorCategoryHint.SERVER -> SyncErrorCategory.SERVER
                SyncErrorCategoryHint.PARSE -> SyncErrorCategory.PARSE
                SyncErrorCategoryHint.CONFLICT -> SyncErrorCategory.CONFLICT
                SyncErrorCategoryHint.STORAGE -> SyncErrorCategory.STORAGE
                SyncErrorCategoryHint.AUTH -> SyncErrorCategory.AUTH
            }
    }
}
