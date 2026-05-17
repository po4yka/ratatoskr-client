package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.api.generated.models.SyncApplyResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SyncApplyResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

/**
 * Regression test for the contract realignment tracked in
 * docs/tasks/issues/fix-kmp-sync-apply-response-dtos-to-match-backend-contract.md.
 *
 * The backend `/v1/sync/apply` response is a `{success, meta, data}` envelope
 * with `data.session_id`, `data.results[]`, `data.conflicts[]`, `data.has_more`.
 * This test pins the deserialization so a future regression that re-introduces
 * the old `applied`/`server_version` shape would fail loudly here instead of
 * silently dropping conflicts at runtime.
 */
class SyncApplyResponseShapeTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun deserializesSessionLevelSuccessShape() {
        val payload = """
            {
              "success": true,
              "data": {
                "session_id": "sess-1",
                "results": [
                  {
                    "id": "42",
                    "entity_type": "summary",
                    "status": "applied",
                    "server_version": 7
                  }
                ],
                "has_more": false
              }
            }
        """.trimIndent()

        val envelope = json.decodeFromString<SyncApplyResponseEnvelope>(payload)
        val data = assertNotNull(envelope.data)
        assertEquals("sess-1", data.sessionId)
        assertEquals(1, data.results.size)
        assertEquals(SyncApplyResult.Status.APPLIED, data.results.single().status)
        assertEquals(false, data.hasMore)
    }

    @Test
    fun deserializesConflictsArraySeparateFromResults() {
        val payload = """
            {
              "success": true,
              "data": {
                "session_id": "sess-2",
                "results": [],
                "conflicts": [
                  {
                    "id": "9",
                    "entity_type": "tag",
                    "status": "conflict",
                    "error_code": "VERSION_MISMATCH",
                    "server_version": 12
                  }
                ],
                "has_more": false
              }
            }
        """.trimIndent()

        val envelope = json.decodeFromString<SyncApplyResponseEnvelope>(payload)
        val data = assertNotNull(envelope.data)
        val conflicts = assertNotNull(data.conflicts)
        assertEquals(1, conflicts.size)
        assertEquals(SyncApplyResult.Status.CONFLICT, conflicts.single().status)
        assertEquals("VERSION_MISMATCH", conflicts.single().errorCode)
    }

    @Test
    fun deserializesHasMoreTrueForPaginatedApply() {
        val payload = """
            {
              "success": true,
              "data": {
                "session_id": "sess-3",
                "results": [],
                "has_more": true
              }
            }
        """.trimIndent()

        val envelope = json.decodeFromString<SyncApplyResponseEnvelope>(payload)
        val data = assertNotNull(envelope.data)
        assertTrue(data.hasMore == true, "has_more=true must survive deserialization")
    }
}
