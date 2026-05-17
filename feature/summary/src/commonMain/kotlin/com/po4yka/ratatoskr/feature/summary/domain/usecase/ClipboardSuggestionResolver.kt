package com.po4yka.ratatoskr.feature.summary.domain.usecase

/**
 * Pure decision-maker for the SummaryListScreen "Submit URL from clipboard"
 * banner.
 *
 * The full banner flow has three platform-side touchpoints:
 *  1. [com.po4yka.ratatoskr.util.share.ClipboardProbe.hasUrl] on resume —
 *     returns a boolean signal **without** triggering the iOS "X pasted from
 *     Y" toast.
 *  2. [resolve] (this function) decides whether to show the banner given
 *     the probe result + the rolling window of dismissed URLs + whether the
 *     URL already exists in the library.
 *  3. Only on the user tapping the banner's Submit button does the ViewModel
 *     call [com.po4yka.ratatoskr.util.share.ClipboardProbe.readUrl], which is
 *     the call that may surface the iOS paste prompt — at which point the
 *     user has clearly consented.
 *
 * Splitting (2) out as a pure function makes the dismissal-window + library
 * cross-check rules testable without spinning up a clipboard, a UserPreferences
 * store, or a Summary repository.
 *
 * Note that [resolve] runs against an opaque [String] rather than the actual
 * URL (which the iOS path won't have at decision time — see ClipboardProbe
 * docstring). On Android both probe calls return the real URL so callers can
 * pass `urlIfKnown = probe.readUrl()`; on iOS the probe only returns the
 * boolean, so callers pass `urlIfKnown = null` and the resolver falls back to
 * the "trust the boolean, defer dedup to readUrl" path.
 */
object ClipboardSuggestionResolver {
    /**
     * Maximum number of recently-dismissed URLs to remember so the banner
     * does not re-prompt for the same URL during the same session.
     *
     * Chose 16 because the iOS pasteboard probe + dismissal scenario is
     * dominated by a handful of frequently-paste-bombed sources (news
     * aggregators, group chats); going higher trades RAM for diminishing
     * returns on dedup quality, going lower lets the same URL re-prompt
     * within a single session.
     */
    const val DISMISSAL_WINDOW: Int = 16

    /**
     * Decides whether to show the clipboard-suggestion banner.
     *
     * @param hasClipboardUrl Result of `ClipboardProbe.hasUrl()`. False
     *        means no banner regardless of other inputs.
     * @param urlIfKnown The clipboard URL when the probe could surface it
     *        cheaply (Android); null on iOS where probe only returns a
     *        boolean. When non-null, used to dedup against
     *        [recentlyDismissed] and [libraryContains].
     * @param recentlyDismissed Rolling window of URLs the user dismissed in
     *        this session (and persisted to UserPreferences). Order doesn't
     *        matter — membership is what counts.
     * @param libraryContains `true` when [urlIfKnown] already exists in the
     *        user's summary library (per a quick SQLDelight lookup keyed on
     *        the source URL). Pass `false` when [urlIfKnown] is null since
     *        the cross-check can't run.
     */
    fun resolve(
        hasClipboardUrl: Boolean,
        urlIfKnown: String?,
        recentlyDismissed: Set<String>,
        libraryContains: Boolean,
    ): Decision =
        when {
            !hasClipboardUrl -> Decision.Hide(reason = HideReason.NO_URL_ON_CLIPBOARD)
            urlIfKnown != null && libraryContains ->
                Decision.Hide(reason = HideReason.ALREADY_IN_LIBRARY)
            urlIfKnown != null && recentlyDismissed.contains(urlIfKnown) ->
                Decision.Hide(reason = HideReason.RECENTLY_DISMISSED)
            else -> Decision.Show(urlIfKnown = urlIfKnown)
        }

    /**
     * Append [url] to the rolling-window dismissal set, trimming the oldest
     * entry when the window is full so set membership stays bounded at
     * [DISMISSAL_WINDOW]. Returns a new list to keep call sites
     * immutable-friendly.
     *
     * The order is FIFO (oldest first), so the caller can persist the list
     * verbatim and reload it as a stable sequence — important when this
     * round-trips through UserPreferences and back.
     */
    fun appendDismissed(
        previous: List<String>,
        url: String,
    ): List<String> {
        val deduped = previous.filterNot { it == url }
        val appended = deduped + url
        return if (appended.size <= DISMISSAL_WINDOW) {
            appended
        } else {
            appended.drop(appended.size - DISMISSAL_WINDOW)
        }
    }

    sealed interface Decision {
        /** Banner should not be shown. [reason] is for logging / telemetry, never user-facing. */
        data class Hide(val reason: HideReason) : Decision

        /**
         * Banner should be shown. [urlIfKnown] is the truncated URL to render
         * inside the brackets when available; on iOS this is null and the
         * banner renders a generic "Submit URL from clipboard" label
         * because surfacing the URL pre-tap would defeat the privacy probe.
         */
        data class Show(val urlIfKnown: String?) : Decision
    }

    enum class HideReason {
        NO_URL_ON_CLIPBOARD,
        ALREADY_IN_LIBRARY,
        RECENTLY_DISMISSED,
    }
}
