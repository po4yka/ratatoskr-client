package com.po4yka.bitesizereader.util.share

import com.po4yka.bitesizereader.domain.model.Summary
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Desktop stub implementation of ShareManager for Compose Hot Reload development
 * Logs share content (for development only)
 */
class DesktopShareManager : ShareManager {
    override fun shareSummary(
        summary: Summary,
        customMessage: String?,
    ) {
        logger.info { "Sharing summary: ${summary.title}" }
        logger.debug { "URL: ${summary.sourceUrl}" }
        logger.debug { "Content: ${summary.content}" }
        customMessage?.let { logger.debug { "Custom message: $it" } }
    }

    override fun shareText(
        text: String,
        subject: String?,
    ) {
        logger.info { "Sharing text: $text" }
        subject?.let { logger.debug { "Subject: $it" } }
    }

    override fun shareUrl(
        url: String,
        title: String?,
    ) {
        logger.info { "Sharing URL: $url" }
        title?.let { logger.debug { "Title: $it" } }
    }
}
