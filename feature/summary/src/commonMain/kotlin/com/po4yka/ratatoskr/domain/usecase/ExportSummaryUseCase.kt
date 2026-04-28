package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.domain.repository.SummaryRepository
import com.po4yka.ratatoskr.util.extractDomain
import org.koin.core.annotation.Factory

private const val MAX_KEY_IDEAS = 3
private const val MAX_TOPIC_TAGS = 3
private const val FALLBACK_CONTENT_MAX_CHARS = 200

/**
 * Formats a locally-cached summary as a rich share preview for social apps.
 * Reads from the local database only — no network call.
 */
@Factory
class ExportSummaryUseCase(private val repository: SummaryRepository) {
    suspend operator fun invoke(id: String): Result<String> =
        runCatching {
            val summary =
                repository.getSummaryById(id)
                    ?: error("Summary not found: $id")

            buildSharePreview(summary)
        }

    private fun buildSharePreview(summary: Summary): String =
        buildString {
            // Title line: use title if available, else URL domain
            val displayTitle =
                summary.title.ifBlank {
                    extractDomain(summary.sourceUrl) ?: summary.sourceUrl
                }
            appendLine("\uD83D\uDCD6 $displayTitle")
            appendLine()

            // TL;DR: use content field (mapped from tldr/summary_250 in the DB).
            // Fall back to first 200 chars of fullContent if content is blank.
            val tldrText =
                summary.content.ifBlank {
                    summary.fullContent?.take(FALLBACK_CONTENT_MAX_CHARS)
                }
            if (!tldrText.isNullOrBlank()) {
                appendLine(tldrText)
                appendLine()
            }

            // Key ideas from insights.newFacts if available
            val keyIdeas =
                summary.insights?.newFacts
                    ?.take(MAX_KEY_IDEAS)
                    ?.map { it.fact }
                    ?.filter { it.isNotBlank() }
                    .orEmpty()

            if (keyIdeas.isNotEmpty()) {
                appendLine("Key ideas:")
                keyIdeas.forEach { idea ->
                    appendLine("\u2022 $idea")
                }
                appendLine()
            }

            // Footer: reading time + tags
            val footerParts = mutableListOf<String>()
            summary.readingTimeMin?.let { footerParts.add("\u23F1 $it min read") }
            val tags = summary.tags.take(MAX_TOPIC_TAGS).filter { it.isNotBlank() }
            if (tags.isNotEmpty()) {
                footerParts.add(tags.joinToString(", "))
            }
            if (footerParts.isNotEmpty()) {
                appendLine(footerParts.joinToString(" \u00B7 "))
                appendLine()
            }

            append("Shared from Ratatoskr")
        }.trimEnd()
}
