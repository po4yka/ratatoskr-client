package com.po4yka.ratatoskr.feature.sync.domain.health

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class SyncHealthSnapshotTest {
    @Test
    fun `empty snapshot reports never-synced and zero queue depth`() {
        val snapshot = SyncHealthSnapshot.empty()

        assertNull(snapshot.lastSyncAt)
        assertTrue(snapshot.appliers.isEmpty())
        assertEquals(0, snapshot.pendingOperationsDepth)
        assertFalse(snapshot.hasAnyFailure)
    }

    @Test
    fun `categorizeError maps a network IOException to NETWORK without exposing the message`() {
        // Regression guard for the spec rule: "No PII in error messages displayed
        // (use category + correlation id, not raw payloads)." The summary must not
        // round-trip the original exception's message, which can contain URLs with
        // bearer tokens, user emails, or other personal data.
        val raw = RuntimeException("https://api.example.com/v1/summary?token=secret123 timed out")
        val summary =
            SyncHealthSnapshot.categorizeError(
                raw = raw,
                correlationId = "corr-abc-123",
                hint = SyncErrorCategoryHint.NETWORK,
            )

        assertEquals(SyncErrorCategory.NETWORK, summary.category)
        assertEquals("corr-abc-123", summary.correlationId)
        assertFalse(summary.toString().contains("secret123"), "raw token must not leak through toString")
        assertFalse(summary.toString().contains("example.com"))
    }

    @Test
    fun `categorizeError maps HTTP hint to SERVER with the correlation id preserved`() {
        val summary =
            SyncHealthSnapshot.categorizeError(
                raw = IllegalStateException("response body: {\"email\":\"u@x.test\"}"),
                correlationId = "trace-42",
                hint = SyncErrorCategoryHint.SERVER,
            )

        assertEquals(SyncErrorCategory.SERVER, summary.category)
        assertEquals("trace-42", summary.correlationId)
        assertFalse(summary.toString().contains("u@x.test"))
    }

    @Test
    fun `categorizeError defaults to UNKNOWN when the call site has no hint`() {
        // Defensive default. The use case must pick an explicit hint where it
        // knows the failure shape; for unexpected paths we don't pretend.
        val summary =
            SyncHealthSnapshot.categorizeError(
                raw = Throwable("generic failure"),
                correlationId = "trace-x",
                hint = null,
            )

        assertEquals(SyncErrorCategory.UNKNOWN, summary.category)
        assertEquals("trace-x", summary.correlationId)
    }

    @Test
    fun `categorizeError trims a too-long correlation id but preserves prefix`() {
        // Defends the debug UI from a misbehaving caller pasting a huge string
        // into the correlation id slot. The prefix is what's actionable for log
        // grep; the suffix is noise.
        val longId = "x".repeat(SyncHealthSnapshot.MAX_CORRELATION_ID_LENGTH + 100)
        val summary =
            SyncHealthSnapshot.categorizeError(
                raw = RuntimeException("oops"),
                correlationId = longId,
                hint = SyncErrorCategoryHint.NETWORK,
            )

        assertEquals(SyncHealthSnapshot.MAX_CORRELATION_ID_LENGTH, summary.correlationId.length)
        assertTrue(summary.correlationId.startsWith("x"))
    }

    @Test
    fun `categorizeError blanks a blank or null correlation id to the canonical sentinel`() {
        // The UI shows a fixed-width column; an empty correlation id breaks the
        // layout. The sentinel "-" stays visually consistent.
        val withBlank =
            SyncHealthSnapshot.categorizeError(
                raw = RuntimeException(),
                correlationId = "   ",
                hint = SyncErrorCategoryHint.NETWORK,
            )
        val withNull =
            SyncHealthSnapshot.categorizeError(
                raw = RuntimeException(),
                correlationId = null,
                hint = SyncErrorCategoryHint.NETWORK,
            )

        assertEquals(SyncHealthSnapshot.MISSING_CORRELATION_ID, withBlank.correlationId)
        assertEquals(SyncHealthSnapshot.MISSING_CORRELATION_ID, withNull.correlationId)
    }

    @Test
    fun `hasAnyFailure is true when at least one applier row has a non-null lastError`() {
        val failing =
            SyncApplierRow(
                name = "summaries",
                lastRunAt = Instant.fromEpochSeconds(1_700_000_000),
                lastError =
                    SyncErrorSummary(
                        category = SyncErrorCategory.NETWORK,
                        correlationId = "abc",
                    ),
                retryCount = 2,
                successCount = 5,
            )
        val healthy =
            SyncApplierRow(
                name = "collections",
                lastRunAt = Instant.fromEpochSeconds(1_700_000_100),
                lastError = null,
                retryCount = 0,
                successCount = 9,
            )

        val snapshot =
            SyncHealthSnapshot(
                lastSyncAt = Instant.fromEpochSeconds(1_700_000_100),
                appliers = listOf(failing, healthy),
                pendingOperationsDepth = 3,
            )

        assertTrue(snapshot.hasAnyFailure)
        assertEquals(2, snapshot.appliers.size)
    }

    @Test
    fun `applier rows render with stable text — name, status icon, retry count, redacted error`() {
        // The Frost-styled debug table consumes this format. The text contract
        // pins the column order so a UI refactor must update tests deliberately.
        val row =
            SyncApplierRow(
                name = "summaries",
                lastRunAt = Instant.fromEpochSeconds(1_700_000_000),
                lastError = SyncErrorSummary(SyncErrorCategory.NETWORK, "corr-1"),
                retryCount = 3,
                successCount = 10,
            )

        val rendered = row.render()
        assertTrue(rendered.contains("summaries"), "applier name appears in render")
        assertTrue(rendered.contains("NETWORK"), "category appears, not raw error")
        assertTrue(rendered.contains("corr-1"), "correlation id appears for log grep")
        assertTrue(rendered.contains("3"), "retry count appears")
        assertFalse(rendered.contains("Exception"), "no raw exception class in render")
    }

    @Test
    fun `healthy applier rows render without an error column`() {
        val row =
            SyncApplierRow(
                name = "collections",
                lastRunAt = Instant.fromEpochSeconds(1_700_000_100),
                lastError = null,
                retryCount = 0,
                successCount = 9,
            )

        val rendered = row.render()
        assertTrue(rendered.contains("collections"))
        assertTrue(rendered.contains("OK"), "healthy rows display the OK marker")
        assertFalse(rendered.contains("NETWORK"))
        assertFalse(rendered.contains("UNKNOWN"))
    }
}
