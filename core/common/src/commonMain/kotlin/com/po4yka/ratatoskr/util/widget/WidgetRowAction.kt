package com.po4yka.ratatoskr.util.widget

/**
 * Typed widget row actions invoked from a home-screen widget row. Two variants
 * today — [MarkRead] and [Archive] — both carrying the target summary id. The
 * sealed contract lets the action callback site call `action.summaryId` without
 * branching on the concrete variant.
 *
 * The codec lives next door because Glance `ActionParameters` and iOS
 * `WidgetKit` intents both bind to string-keyed pairs at the platform edge.
 * Keeping the encode/decode pure (no Glance types) means the same atom can be
 * reused by an iOS interactive-widget variant when that lands; only the
 * platform-edge shim has to know about ActionParameters / Intents.
 *
 * `decode` returns null on any malformed input (missing keys, blank id, unknown
 * action type) — defends the callback against partial bundles after process
 * death (Android) and forward-compat with future action types.
 */
sealed interface WidgetRowAction {
    val summaryId: String

    data class MarkRead(override val summaryId: String) : WidgetRowAction

    data class Archive(override val summaryId: String) : WidgetRowAction
}

object WidgetRowActionCodec {
    const val ACTION_KEY: String = "ratatoskr.widget.action"
    const val SUMMARY_ID_KEY: String = "ratatoskr.widget.summary_id"
    private const val MARK_READ_WIRE = "MARK_READ"
    private const val ARCHIVE_WIRE = "ARCHIVE"

    fun encode(action: WidgetRowAction): Map<String, String> =
        mapOf(
            ACTION_KEY to action.wireValue(),
            SUMMARY_ID_KEY to action.summaryId,
        )

    fun decode(params: Map<String, String>): WidgetRowAction? {
        val actionWire = params[ACTION_KEY] ?: return null
        val summaryId = params[SUMMARY_ID_KEY]?.trim().orEmpty()
        if (summaryId.isEmpty()) return null
        return when (actionWire) {
            MARK_READ_WIRE -> WidgetRowAction.MarkRead(summaryId)
            ARCHIVE_WIRE -> WidgetRowAction.Archive(summaryId)
            else -> null
        }
    }

    private fun WidgetRowAction.wireValue(): String =
        when (this) {
            is WidgetRowAction.MarkRead -> MARK_READ_WIRE
            is WidgetRowAction.Archive -> ARCHIVE_WIRE
        }
}
