package com.po4yka.ratatoskr.util.widget

/**
 * Persisted filter for the home-screen `RecentSummariesWidget`. Picked by
 * the user in `RecentSummariesWidgetConfigActivity` and round-tripped
 * through Glance `GlanceStateDefinition` (Android) or the App Group
 * defaults (iOS).
 *
 * Three states match the picker:
 *  - [All]                 — default; no filter, show recent across the library.
 *  - [Tag]                 — filter to summaries carrying a given tag.
 *  - [Collection]          — filter to summaries inside a collection.
 *
 * Identifier strings are kept as opaque strings rather than typed IDs so
 * the atom stays free of `core/data` domain imports. The repository
 * lookup layer is responsible for resolving the string to a typed row.
 */
sealed interface WidgetFilter {
    data object All : WidgetFilter

    data class Tag(val tagName: String) : WidgetFilter

    data class Collection(val collectionId: String) : WidgetFilter
}

/**
 * String-keyed encode/decode for [WidgetFilter]. Mirrors the pattern in
 * [WidgetRowActionCodec] — pure, no platform types, so the same atom
 * drives the Glance config persistence and the iOS App Group payload.
 *
 * `decode` is forgiving on purpose: missing kind, unknown kind, missing
 * payload, blank payload all coerce to [WidgetFilter.All]. The widget
 * must always have something to render — silently hiding every summary
 * because of a corrupt save-state is worse than showing the unfiltered
 * default.
 */
object WidgetFilterCodec {
    const val FILTER_KIND_KEY: String = "ratatoskr.widget.filter_kind"
    const val PAYLOAD_KEY: String = "ratatoskr.widget.filter_payload"
    private const val KIND_ALL = "ALL"
    private const val KIND_TAG = "TAG"
    private const val KIND_COLLECTION = "COLLECTION"

    fun encode(filter: WidgetFilter): Map<String, String> =
        when (filter) {
            WidgetFilter.All -> mapOf(FILTER_KIND_KEY to KIND_ALL)
            is WidgetFilter.Tag ->
                mapOf(
                    FILTER_KIND_KEY to KIND_TAG,
                    PAYLOAD_KEY to filter.tagName,
                )
            is WidgetFilter.Collection ->
                mapOf(
                    FILTER_KIND_KEY to KIND_COLLECTION,
                    PAYLOAD_KEY to filter.collectionId,
                )
        }

    fun decode(params: Map<String, String>): WidgetFilter {
        val kind = params[FILTER_KIND_KEY] ?: return WidgetFilter.All
        val payload = params[PAYLOAD_KEY]?.trim().orEmpty()
        return when (kind) {
            KIND_TAG -> if (payload.isEmpty()) WidgetFilter.All else WidgetFilter.Tag(payload)
            KIND_COLLECTION -> if (payload.isEmpty()) WidgetFilter.All else WidgetFilter.Collection(payload)
            KIND_ALL -> WidgetFilter.All
            else -> WidgetFilter.All
        }
    }
}
