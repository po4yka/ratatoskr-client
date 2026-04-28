package com.po4yka.ratatoskr.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

/** Unit tests for the Summary domain model */
class SummaryTest {
    @Test
    fun `Summary model has correct values`() {
        // Given
        val now = Clock.System.now()
        val summary =
            Summary(
                id = "1",
                title = "Test Article",
                content = "Test content",
                sourceUrl = "https://example.com/article",
                imageUrl = "https://example.com/image.jpg",
                createdAt = now,
                isRead = false,
                tags = listOf("tech", "ai"),
            )

        // Then
        assertEquals("1", summary.id)
        assertEquals("Test Article", summary.title)
        assertEquals("Test content", summary.content)
        assertEquals("https://example.com/article", summary.sourceUrl)
        assertEquals("https://example.com/image.jpg", summary.imageUrl)
        assertFalse(summary.isRead)
        assertEquals(listOf("tech", "ai"), summary.tags)
    }

    @Test
    fun `Summary can have null imageUrl`() {
        // Given
        val now = Clock.System.now()
        val summary =
            Summary(
                id = "2",
                title = "No Image Article",
                content = "Content without image",
                sourceUrl = "https://example.com/no-image",
                imageUrl = null,
                createdAt = now,
                isRead = false,
                tags = emptyList(),
            )

        // Then
        assertNull(summary.imageUrl)
    }

    @Test
    fun `Summary can have empty tags`() {
        // Given
        val now = Clock.System.now()
        val summary =
            Summary(
                id = "3",
                title = "Untagged Article",
                content = "Content",
                sourceUrl = "https://example.com/untagged",
                imageUrl = null,
                createdAt = now,
                isRead = false,
                tags = emptyList(),
            )

        // Then
        assertTrue(summary.tags.isEmpty())
    }

    @Test
    fun `Summary isRead can be true`() {
        // Given
        val now = Clock.System.now()
        val summary =
            Summary(
                id = "4",
                title = "Read Article",
                content = "Already read",
                sourceUrl = "https://example.com/read",
                imageUrl = null,
                createdAt = now,
                isRead = true,
                tags = emptyList(),
            )

        // Then
        assertTrue(summary.isRead)
    }
}
