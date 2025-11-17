package com.po4yka.bitesizereader.util.share

import com.po4yka.bitesizereader.domain.model.Summary
import platform.Foundation.NSURL

/**
 * iOS implementation of ShareManager using UIActivityViewController
 *
 * Note: On iOS, we need to present the share sheet from a view controller.
 * The actual presentation is handled in Swift code via ShareHelper.
 */
class IosShareManager : ShareManager {
    override fun shareSummary(
        summary: Summary,
        customMessage: String?,
    ) {
        val shareText = summary.toShareText(customMessage)
        val url = NSURL.URLWithString(summary.url)

        // Call the Swift ShareHelper to present the share sheet
        shareItems(listOfNotNull(shareText, url))
    }

    override fun shareText(
        text: String,
        subject: String?,
    ) {
        val shareText =
            if (subject != null) {
                "$subject\n\n$text"
            } else {
                text
            }
        shareItems(listOf(shareText))
    }

    override fun shareUrl(
        url: String,
        title: String?,
    ) {
        val nsUrl = NSURL.URLWithString(url)
        val items =
            if (title != null) {
                listOfNotNull(title, nsUrl)
            } else {
                listOfNotNull(nsUrl)
            }
        shareItems(items)
    }

    /**
     * Share items using UIActivityViewController
     * This will be called from Swift code to properly present the share sheet
     */
    private fun shareItems(items: List<Any>) {
        // Note: The actual presentation of UIActivityViewController
        // needs to be done from Swift code since it requires a
        // UIViewController to present from
        //
        // We'll create a companion function that can be called from Swift
        ShareHelper.share(items)
    }
}

/**
 * Helper object for iOS sharing
 * This is accessed from Swift code
 */
object ShareHelper {
    private var pendingItems: List<Any>? = null

    fun share(items: List<Any>) {
        pendingItems = items
        // Notify Swift code that there are items to share
        // Swift will check for pending items and present the share sheet
    }

    fun getPendingItems(): List<Any>? {
        val items = pendingItems
        pendingItems = null
        return items
    }
}
