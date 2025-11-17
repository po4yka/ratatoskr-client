package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.EntitiesDto
import com.po4yka.bitesizereader.data.remote.dto.KeyStatDto
import com.po4yka.bitesizereader.data.remote.dto.ReadabilityDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryCompactDto
import com.po4yka.bitesizereader.data.remote.dto.SummaryDetailDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for Summary mappers
 */
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
        assertEquals(1, domain.id)
        assertEquals(100, domain.requestId)
        assertEquals("Test Article", domain.title)
        assertEquals("https://example.com/article", domain.url)
        assertEquals("example.com", domain.domain)
        assertEquals("Test TLDR", domain.tldr)
        assertEquals("Short summary", domain.summary250)
        assertEquals(listOf("tech", "ai"), domain.topicTags)
        assertEquals(5, domain.readingTimeMin)
        assertEquals("en", domain.lang)
        assertEquals(false, domain.isRead)
        assertNotNull(domain.createdAt)

        // Compact DTO doesn't include these fields
        assertNull(domain.summary1000)
        assertTrue(domain.keyIdeas.isEmpty())
        assertTrue(domain.answeredQuestions.isEmpty())
    }

    @Test
    fun `SummaryDetailDto maps to domain model correctly with all fields`() {
        // Given
        val dto =
            SummaryDetailDto(
                id = 1,
                requestId = 100,
                title = "Test Article",
                url = "https://example.com/article",
                domain = "example.com",
                tldr = "Test TLDR",
                summary250 = "Short summary",
                summary1000 = "Long summary",
                keyIdeas = listOf("Idea 1", "Idea 2"),
                topicTags = listOf("tech", "ai"),
                answeredQuestions = listOf("Question 1?", "Question 2?"),
                seoKeywords = listOf("keyword1", "keyword2"),
                readingTimeMin = 10,
                lang = "en",
                entities = EntitiesDto(people = listOf("John Doe")),
                keyStats = listOf(KeyStatDto(label = "Users", value = 1000000.0, unit = "users")),
                readability = ReadabilityDto(method = "flesch", score = 75.5, level = "college"),
                isRead = true,
                createdAt = "2025-01-15T12:00:00Z",
                updatedAt = "2025-01-16T12:00:00Z",
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(1, domain.id)
        assertEquals("Long summary", domain.summary1000)
        assertEquals(listOf("Idea 1", "Idea 2"), domain.keyIdeas)
        assertEquals(listOf("Question 1?", "Question 2?"), domain.answeredQuestions)
        assertEquals(listOf("keyword1", "keyword2"), domain.seoKeywords)
        assertNotNull(domain.entities)
        assertEquals(listOf("John Doe"), domain.entities?.people)
        assertTrue(domain.isRead)
        assertNotNull(domain.updatedAt)
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
        assertTrue(domain.topicTags.isEmpty())
        assertTrue(domain.keyIdeas.isEmpty())
        assertTrue(domain.answeredQuestions.isEmpty())
    }

    @Test
    fun `SummaryDetailDto handles null optional fields correctly`() {
        // Given
        val dto =
            SummaryDetailDto(
                id = 1,
                requestId = 100,
                title = "Test",
                url = "https://example.com",
                domain = "example.com",
                tldr = "TLDR",
                summary250 = "Summary",
                summary1000 = null,
                keyIdeas = emptyList(),
                topicTags = emptyList(),
                answeredQuestions = emptyList(),
                seoKeywords = emptyList(),
                readingTimeMin = 1,
                lang = "en",
                entities = null,
                keyStats = emptyList(),
                readability = null,
                isRead = false,
                createdAt = "2025-01-15T12:00:00Z",
                updatedAt = null,
            )

        // When
        val domain = dto.toDomain()

        // Then
        assertNull(domain.summary1000)
        assertNull(domain.entities)
        assertNull(domain.readability)
        assertNull(domain.updatedAt)
    }
}
