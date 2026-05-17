package com.po4yka.ratatoskr.util.share

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.UIKit.UIPasteboard
import platform.UIKit.UIPasteboardDetectionPatternProbableWebURL
import kotlin.coroutines.resume

/**
 * iOS-backed [ClipboardProbe]. The two-stage contract matters here:
 *
 * - [hasUrl] uses `UIPasteboard.detectPatternsForPatterns(...)` (iOS 14+) which returns a
 *   boolean signal without exposing the clipboard's actual contents — this is what keeps the
 *   "Submit URL" banner from triggering the system "X pasted from Y" toast on every resume.
 * - [readUrl] reads `UIPasteboard.string` directly and may trigger that toast; only call it
 *   after the user explicitly accepts the banner.
 */
class IosClipboardProbe : ClipboardProbe {
    override suspend fun hasUrl(): Boolean {
        val patterns = setOf(UIPasteboardDetectionPatternProbableWebURL)
        return suspendCancellableCoroutine { continuation ->
            UIPasteboard.generalPasteboard.detectPatternsForPatterns(
                patterns = patterns,
                completionHandler = { detected: Set<*>?, _: NSError? ->
                    val hasUrl = detected?.contains(UIPasteboardDetectionPatternProbableWebURL) == true
                    continuation.resume(hasUrl)
                },
            )
        }
    }

    override suspend fun readUrl(): String? =
        UIPasteboard.generalPasteboard.string?.let { ClipboardUrlParser.firstHttpUrl(it) }
}
