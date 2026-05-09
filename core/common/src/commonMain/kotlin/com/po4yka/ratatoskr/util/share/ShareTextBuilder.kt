package com.po4yka.ratatoskr.util.share

import com.po4yka.ratatoskr.domain.model.Summary

fun buildShareText(
    summary: Summary,
    customMessage: String? = null,
): String =
    buildString {
        if (!customMessage.isNullOrBlank()) {
            appendLine(customMessage)
            appendLine()
        }
        appendLine(summary.title)
        appendLine()
        if (summary.content.isNotBlank()) {
            appendLine("Summary:")
            appendLine(summary.content)
            appendLine()
        }
        appendLine("Read more: ${summary.sourceUrl}")
        if (summary.tags.isNotEmpty()) {
            appendLine()
            append("Tags: ${summary.tags.joinToString(", ") { "#$it" }}")
        }
    }
