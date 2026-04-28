package com.po4yka.ratatoskr.util.share

import android.content.Context
import android.content.Intent
import com.po4yka.ratatoskr.domain.model.Summary

/**
 * Android implementation of ShareManager using Android's share intent
 */
class AndroidShareManager(private val context: Context) : ShareManager {
    override fun shareSummary(
        summary: Summary,
        customMessage: String?,
    ) {
        val shareText = buildShareText(summary, customMessage)
        val shareIntent =
            createShareIntent(
                text = shareText,
                subject = summary.title,
            )
        context.startActivity(shareIntent)
    }

    override fun shareText(
        text: String,
        subject: String?,
    ) {
        val shareIntent = createShareIntent(text = text, subject = subject)
        context.startActivity(shareIntent)
    }

    override fun shareUrl(
        url: String,
        title: String?,
    ) {
        val shareText =
            buildString {
                if (title != null) {
                    appendLine(title)
                    appendLine()
                }
                append(url)
            }
        val shareIntent =
            createShareIntent(
                text = shareText,
                subject = title ?: "Check this out",
            )
        context.startActivity(shareIntent)
    }

    private fun buildShareText(
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

    /**
     * Create an Android share intent
     */
    private fun createShareIntent(
        text: String,
        subject: String?,
    ): Intent {
        val sendIntent =
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                if (subject != null) {
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                }
            }

        return Intent.createChooser(sendIntent, "Share via").apply {
            // Add FLAG_ACTIVITY_NEW_TASK for starting from non-Activity context
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
