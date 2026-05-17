package com.po4yka.ratatoskr.util.observability

/**
 * Pure correlation-id minter for sync appliers and outbound HTTP requests.
 *
 * Produces a short, log-greppable id from (epochMillis, sequence). The
 * `"t-"` prefix lets a single `grep -E 't-[a-z0-9-]+'` find correlation
 * ids without false positives on plain base36 sequences elsewhere in the
 * line. The sequence component disambiguates retries that share the same
 * wall-clock millisecond.
 *
 * Format: `t-{base36(millis)}-{base36(sequence) padded to 2}`. Example:
 * `(1_700_000_000_000, 0)` → `t-lh5qbxk00-00`.
 *
 * Defensive: negative `epochMillis` or `sequence` are clamped to `0` so a
 * brief clock-skew event or a buggy caller never produces an id with a
 * `-` in the middle of a numeric segment.
 *
 * Pure, side-effect-free, deterministic. The atom does not generate the
 * timestamp itself so tests don't depend on a clock and so the same
 * function is callable from production (`Clock.System.now().toEpochMilliseconds()`)
 * and from deterministic replay (a fixture millis value).
 */
object CorrelationIdMint {
    const val PREFIX: String = "t"
    const val MAX_LENGTH: Int = 32

    fun mint(
        epochMillis: Long,
        sequence: Long,
    ): String {
        val safeMillis = if (epochMillis < 0) 0L else epochMillis
        val safeSequence = if (sequence < 0) 0L else sequence
        val timestampPart = safeMillis.toString(radix = 36)
        val sequencePart = safeSequence.toString(radix = 36).padStart(2, '0')
        return "$PREFIX-$timestampPart-$sequencePart"
    }
}
