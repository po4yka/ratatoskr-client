package com.po4yka.ratatoskr.util.observability

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CorrelationIdMintTest {
    @Test
    fun `minted id starts with the t- prefix for log greppability`() {
        // The "t-" prefix lets `grep -E 't-[a-z0-9]+'` find correlation
        // IDs in logs without false-positive matches on plain base36
        // sequences elsewhere in the line.
        val id = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 0)
        assertTrue(id.startsWith("t-"), "id '$id' missing required 't-' prefix")
    }

    @Test
    fun `mint is deterministic — same inputs map to same id`() {
        val a = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 5)
        val b = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 5)
        assertEquals(a, b)
    }

    @Test
    fun `same millis different sequence — distinct ids`() {
        // Pin retry-disambiguation: a retry on the same wall-clock
        // millisecond must produce a different correlation id so a
        // log row from the retry is not conflated with the first try.
        val first = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 0)
        val retry = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 1)
        assertNotEquals(first, retry)
    }

    @Test
    fun `different millis same sequence — distinct ids`() {
        val a = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 0)
        val b = CorrelationIdMint.mint(epochMillis = 1_700_000_000_001, sequence = 0)
        assertNotEquals(a, b)
    }

    @Test
    fun `sequence values 0 through 35 are all distinct in base36`() {
        // The single-char base36 sequence range covers a typical
        // retry budget (0..9) comfortably. Pin no collisions across
        // a fuller range so a future retry-policy change doesn't
        // accidentally collapse two retries to the same id.
        val ids = (0L..35L).map { CorrelationIdMint.mint(epochMillis = 0, sequence = it) }
        assertEquals(36, ids.toSet().size, "ids: $ids")
    }

    @Test
    fun `negative millis is clamped to zero — defensive`() {
        // Some clock-skew scenarios surface negative millis briefly.
        // Don't blow up — clamp and continue.
        val id = CorrelationIdMint.mint(epochMillis = -1, sequence = 0)
        val zeroId = CorrelationIdMint.mint(epochMillis = 0, sequence = 0)
        assertEquals(zeroId, id)
    }

    @Test
    fun `negative sequence is clamped to zero — defensive`() {
        val id = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = -1)
        val zeroSeq = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 0)
        assertEquals(zeroSeq, id)
    }

    @Test
    fun `minted id length is bounded — keeps log lines readable`() {
        // Even a far-future millis + 6-digit retry count must stay
        // under MAX_LENGTH so log columns aren't blown out.
        val id = CorrelationIdMint.mint(epochMillis = Long.MAX_VALUE, sequence = Long.MAX_VALUE)
        assertTrue(
            id.length <= CorrelationIdMint.MAX_LENGTH,
            "id '$id' length=${id.length} exceeds MAX_LENGTH=${CorrelationIdMint.MAX_LENGTH}",
        )
    }

    @Test
    fun `id contains exactly two hyphens — prefix and ts-seq separator`() {
        // Pin the format: t-<ts>-<seq>. A future change that swaps
        // the separator must update the parsers too.
        val id = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 5)
        assertEquals(2, id.count { it == '-' }, "id '$id' should contain exactly two '-' chars")
    }

    @Test
    fun `id parts are lowercase alphanumeric only`() {
        // base36 toString() emits lowercase by default; pin that.
        // Mixed case would break case-sensitive log greps.
        val id = CorrelationIdMint.mint(epochMillis = 1_700_000_000_000, sequence = 35)
        val payload = id.removePrefix("t-")
        assertTrue(
            payload.all { it.isDigit() || (it in 'a'..'z') || it == '-' },
            "payload '$payload' contains non-lowercase-alphanumeric chars",
        )
    }
}
