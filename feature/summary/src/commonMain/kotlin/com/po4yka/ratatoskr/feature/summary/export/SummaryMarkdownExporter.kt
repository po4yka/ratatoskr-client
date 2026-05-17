package com.po4yka.ratatoskr.feature.summary.export

import com.po4yka.ratatoskr.domain.model.Summary

object SummaryMarkdownExporter {
    fun toMarkdown(summary: Summary): String =
        buildString {
            appendFrontmatter(summary)
            append('\n')
            append("# ").append(summary.title).append('\n').append('\n')

            val hasFullContent = summary.isFullContentCached && !summary.fullContent.isNullOrBlank()
            if (hasFullContent) {
                append("## TLDR").append('\n').append('\n')
                append(summary.content).append('\n').append('\n')
                append("## Full content").append('\n').append('\n')
                append(summary.fullContent).append('\n').append('\n')
            } else {
                append(summary.content).append('\n').append('\n')
            }

            val atoms = summary.insights?.newFacts?.map { it.fact }.orEmpty()
            if (atoms.isNotEmpty()) {
                append("## Atoms").append('\n').append('\n')
                atoms.forEach { append("- ").append(it).append('\n') }
                append('\n')
            }

            append("## Source").append('\n').append('\n')
            append("<").append(summary.sourceUrl).append(">").append('\n')
        }

    private fun StringBuilder.appendFrontmatter(summary: Summary) {
        append("---").append('\n')
        append("title: ").append(yamlQuote(summary.title)).append('\n')
        append("source: ").append(yamlQuote(summary.sourceUrl)).append('\n')
        append("created: ").append(yamlQuote(summary.createdAt.toString())).append('\n')
        append("tags: ").append(yamlInlineList(summary.tags)).append('\n')
        summary.readingTimeMin?.let {
            append("reading_time_min: ").append(it).append('\n')
        }
        summary.confidence?.let {
            append("confidence: ").append(it).append('\n')
        }
        summary.quality?.authorBias?.let {
            append("quality.author_bias: ").append(yamlQuote(it)).append('\n')
        }
        summary.quality?.evidenceQuality?.let {
            append("quality.evidence_quality: ").append(yamlQuote(it)).append('\n')
        }
        append("---").append('\n')
    }

    private fun yamlQuote(value: String): String {
        val escaped =
            value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
        return "\"$escaped\""
    }

    private fun yamlInlineList(tags: List<String>): String = tags.joinToString(prefix = "[", postfix = "]")
}
