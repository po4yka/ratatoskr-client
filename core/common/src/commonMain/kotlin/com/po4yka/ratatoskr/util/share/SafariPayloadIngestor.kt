package com.po4yka.ratatoskr.util.share

import com.po4yka.ratatoskr.util.deeplink.DeeplinkNavIntent
import com.po4yka.ratatoskr.util.url.SubmittedUrlNormalizer

/**
 * Pure ingestor that composes three atoms — [SafariPagePayloadValidator],
 * [SubmittedUrlNormalizer], and the [DeeplinkNavIntent] shape — into a
 * single entry point the iOS Safari Action Extension can dispatch:
 *
 *  1. The validator rejects null/blank/non-http payloads.
 *  2. The normalizer upgrades `http://` to `https://` and rejects bare-
 *     host phishing shapes.
 *  3. The result is wrapped in [DeeplinkNavIntent.PrefillSubmitUrl] on
 *     success, or [DeeplinkNavIntent.Drop] on any rejection — matching
 *     the contract used by the deep-link entry path so the iOS host can
 *     dispatch both sources through one router.
 *
 * The title from the Safari page-context is intentionally discarded —
 * the downstream submit flow re-fetches the canonical title server-side,
 * and using the Safari-supplied title would cause inconsistencies between
 * extension-submitted and share-sheet-submitted entries.
 *
 * Pure, side-effect-free, deterministic.
 */
object SafariPayloadIngestor {
    fun ingest(
        rawTitle: String?,
        rawUrl: String?,
    ): DeeplinkNavIntent {
        val payload =
            SafariPagePayloadValidator.validate(rawTitle = rawTitle, rawUrl = rawUrl)
                ?: return DeeplinkNavIntent.Drop

        return when (val normalized = SubmittedUrlNormalizer.normalize(payload.url)) {
            is SubmittedUrlNormalizer.Result.Normalized ->
                DeeplinkNavIntent.PrefillSubmitUrl(url = normalized.url)
            SubmittedUrlNormalizer.Result.Unsupported,
            SubmittedUrlNormalizer.Result.Invalid,
            -> DeeplinkNavIntent.Drop
        }
    }
}
