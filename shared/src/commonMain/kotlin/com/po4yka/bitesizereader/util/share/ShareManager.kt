package com.po4yka.bitesizereader.util.share

import com.po4yka.bitesizereader.domain.model.Summary

/**
 * Platform-agnostic interface for sharing content
 */
interface ShareManager {
    /**
     * Share a summary with title, URL, and optional message
     *
     * @param summary The summary to share
     * @param customMessage Optional custom message to include
     */
    fun shareSummary(
        summary: Summary,
        customMessage: String? = null,
    )

    /**
     * Share plain text content
     *
     * @param text The text to share
     * @param subject Optional subject/title
     */
    fun shareText(
        text: String,
        subject: String? = null,
    )

    /**
     * Share a URL
     *
     * @param url The URL to share
     * @param title Optional title for the URL
     */
    fun shareUrl(
        url: String,
        title: String? = null,
    )
}

/**
 * Build a shareable text from a summary
 */
fun Summary.toShareText(customMessage: String? = null): String {
    return buildString {
        if (!customMessage.isNullOrBlank()) {
            appendLine(customMessage)
            appendLine()
        }

        appendLine(title)
        appendLine()

        // Use available fields from current Summary model
        // Note: tldr, keyIdeas, topicTags might not be present in the new Summary model
        // Adjusting to what's available: content, sourceUrl, tags

        if (content.isNotBlank()) {
            appendLine("Summary:")
            appendLine(content)
            appendLine()
        }

        appendLine("Read more: $sourceUrl")

        if (tags.isNotEmpty()) {
            appendLine()
            append("Tags: ${tags.joinToString(", ") { "#$it" }}")
        }
    }
}
