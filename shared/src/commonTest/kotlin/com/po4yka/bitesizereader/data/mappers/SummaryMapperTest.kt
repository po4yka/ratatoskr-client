package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Unit tests for Summary mappers */
class SummaryMapperTest {
    @Test
    fun `SummaryCompactDto maps to domain model correctly`() {
        // Given
        val dto =
            SummaryCompactDto(
                id = 1,
                requestId = 100,
                title = "Test Article",
                url = "https://example.com/article",
                domain = "example.com",
                tldr = "Test TLDR",
                summary250 = "Short summary",
                topicTags = listOf("tech", "ai"),
                readingTimeMin = 5,
                lang = "en",
                isRead = false,
                createdAt = "2025-01-15T12:00:00Z",
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("1", domain.id)
        assertEquals("Test Article", domain.title)
        assertEquals("https://example.com/article", domain.sourceUrl)
        assertEquals(false, domain.isRead)
        assertNotNull(domain.createdAt)
        assertEquals(listOf("tech", "ai"), domain.tags)
    }

    @Test
    fun `SummaryCompactDto uses tldr when summary250 is null`() {
        // Given
        val dto =
            SummaryCompactDto(
                id = 1,
                requestId = 100,
                title = "Test Article",
                url = "https://example.com/article",
                domain = "example.com",
                tldr = "Test TLDR content",
                summary250 = null,
                topicTags = listOf("tech"),
                readingTimeMin = 5,
                lang = "en",
                isRead = false,
                createdAt = "2025-01-15T12:00:00Z",
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals("Test TLDR content", domain.content)
    }

    @Test
    fun `SummaryCompactDto handles empty lists correctly`() {
        // Given
        val dto =
            SummaryCompactDto(
                id = 1,
                requestId = 100,
                title = "Test",
                url = "https://example.com",
                domain = "example.com",
                tldr = "TLDR",
                summary250 = "Summary",
                topicTags = emptyList(),
                readingTimeMin = 1,
                lang = "en",
                isRead = false,
                createdAt = "2025-01-15T12:00:00Z",
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertTrue(domain.tags.isEmpty())
    }
}
