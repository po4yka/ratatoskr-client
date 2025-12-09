package com.po4yka.bitesizereader.util.share

import com.po4yka.bitesizereader.domain.model.Summary

/**
 * Desktop stub implementation of ShareManager for Compose Hot Reload development
 * Prints share content to console (for development only)
 */
class DesktopShareManager : ShareManager {
    override fun shareSummary(
        summary: Summary,
        customMessage: String?,
    ) {
        println("Sharing summary: ${summary.title}")
        println("URL: ${summary.sourceUrl}")
        println("Content: ${summary.content}")
        customMessage?.let { println("Custom message: $it") }
    }

    override fun shareText(
        text: String,
        subject: String?,
    ) {
        println("Sharing text: $text")
        subject?.let { println("Subject: $it") }
    }

    override fun shareUrl(
        url: String,
        title: String?,
    ) {
        println("Sharing URL: $url")
        title?.let { println("Title: $it") }
    }
}
