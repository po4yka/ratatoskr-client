package com.po4yka.ratatoskr.util.share

import com.po4yka.ratatoskr.domain.model.Summary

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
