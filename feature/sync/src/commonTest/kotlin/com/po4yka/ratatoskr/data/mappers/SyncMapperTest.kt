package com.po4yka.ratatoskr.data.mappers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Regression coverage for `JsonObject.toSummaryEntity` — the mapper that
 * turns sync-summary JSON payloads into [com.po4yka.ratatoskr.database.SummaryEntity].
 *
 * Pinned because the mapper is the only translation layer between the backend
 * wire shape and the local DB row; a silent fallback (e.g. swallowing a bad
 * `created_at`) would corrupt the reading-timeline and persist forever.
 */
class SyncMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun parse(literal: String): JsonObject = json.parseToJsonElement(literal) as JsonObject

    @Test
    fun prefersSummary1000WhenBothFieldsPresent() {
        val payload =
            parse(
                """
                {
                  "json_payload": {
                    "summary_1000": "Long form",
                    "summary_250": "Short form"
                  },
                  "created_at": "2024-12-14T10:20:00Z"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 1L))
        assertEquals("Long form", entity.content)
    }

    @Test
    fun fallsBackToSummary250WhenSummary1000Absent() {
        val payload =
            parse(
                """
                {
                  "json_payload": {
                    "summary_250": "Short form"
                  },
                  "created_at": "2024-12-14T10:20:00Z"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 2L))
        assertEquals("Short form", entity.content)
    }

    @Test
    fun emptyContentWhenBothSummaryFieldsAbsent() {
        val payload =
            parse(
                """
                {
                  "json_payload": {},
                  "created_at": "2024-12-14T10:20:00Z"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 3L))
        assertEquals("", entity.content)
        // readingTimeMin is derived from content length; zero-length → null
        assertNull(entity.readingTimeMin)
    }

    @Test
    fun malformedCreatedAtStillProducesEntityWithFallbackTimestamp() {
        // The mapper logs and substitutes Clock.System.now() rather than
        // returning null — the test asserts the row still maps successfully
        // so a single bad timestamp can't black-hole an entire sync batch.
        val before = kotlin.time.Clock.System.now()
        val payload =
            parse(
                """
                {
                  "json_payload": { "summary_250": "hello" },
                  "created_at": "definitely-not-iso-8601"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 4L))
        val after = kotlin.time.Clock.System.now()
        // Substituted timestamp must be roughly now (between before and after).
        assert(entity.createdAt >= before && entity.createdAt <= after) {
            "createdAt fallback ${entity.createdAt} not within [$before, $after]"
        }
    }

    @Test
    fun booleansDefaultToFalseWhenAbsent() {
        val payload =
            parse(
                """
                {
                  "json_payload": { "summary_250": "hello" },
                  "created_at": "2024-12-14T10:20:00Z"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 5L))
        assertFalse(entity.isRead)
        assertFalse(entity.isFavorited)
        assertFalse(entity.isArchived)
        assertEquals(0, entity.lastReadPosition)
        assertEquals(0, entity.lastReadOffset)
    }

    @Test
    fun stripsHashPrefixFromTopicTags() {
        val payload =
            parse(
                """
                {
                  "json_payload": {
                    "summary_250": "hello",
                    "topic_tags": ["#kotlin", "#sync", "no-hash"]
                  },
                  "created_at": "2024-12-14T10:20:00Z"
                }
                """.trimIndent(),
            )
        val entity = assertNotNull(payload.toSummaryEntity(id = 6L))
        assertEquals(listOf("kotlin", "sync", "no-hash"), entity.tags)
    }
}
