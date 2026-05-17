package com.po4yka.ratatoskr.util.deeplink

/**
 * Nav-layer intent produced by [DeeplinkNavRouter] — a platform-agnostic
 * description of what the shell should do in response to a parsed deep
 * link. Both Android (MainActivity intent dispatch) and iOS (NSUserActivity
 * continuation) translate this into their own navigation primitives.
 */
sealed interface DeeplinkNavIntent {
    data class OpenSummary(val summaryId: String) : DeeplinkNavIntent

    data class PrefillSubmitUrl(val url: String) : DeeplinkNavIntent

    data object Drop : DeeplinkNavIntent
}

/**
 * Maps a parsed [RatatoskrDeepLink] into a platform-agnostic
 * [DeeplinkNavIntent] that the shell layer can dispatch without
 * knowing about the parser's sealed hierarchy.
 *
 * Design notes:
 *  - Defensive trim + blank-check on summary id and url. The parser is
 *    expected to reject blank inputs upstream, but the router adds a
 *    second guard so a future parser regression never causes a phantom
 *    navigation (e.g. "summary id =  ") that would land on a not-found
 *    screen with a confusing log line.
 *  - [RatatoskrDeepLink.Unknown] always maps to [DeeplinkNavIntent.Drop].
 *    Platform hosts are expected to silently ignore unknown deep links —
 *    never throw, never user-toast, never crash.
 *
 * Pure, side-effect-free, deterministic.
 */
object DeeplinkNavRouter {
    fun routeFor(deeplink: RatatoskrDeepLink): DeeplinkNavIntent =
        when (deeplink) {
            is RatatoskrDeepLink.OpenSummary -> {
                val cleaned = deeplink.id.trim()
                if (cleaned.isEmpty()) {
                    DeeplinkNavIntent.Drop
                } else {
                    DeeplinkNavIntent.OpenSummary(summaryId = cleaned)
                }
            }
            is RatatoskrDeepLink.SubmitUrl -> {
                val cleaned = deeplink.url.trim()
                if (cleaned.isEmpty()) {
                    DeeplinkNavIntent.Drop
                } else {
                    DeeplinkNavIntent.PrefillSubmitUrl(url = cleaned)
                }
            }
            is RatatoskrDeepLink.Unknown -> DeeplinkNavIntent.Drop
        }
}
