package com.po4yka.bitesizereader.domain.model

import com.po4yka.bitesizereader.util.MockDataFactory
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

/**
 * Unit tests for Summary domain model
 */
class SummaryTest {

    @Test
    fun `Summary is created with correct properties`() {
        // Given
        val summary = MockDataFactory.createSummary(
            id = 1,
            title = "Test Article",
            isRead = false
        )

        // Then
        assertEquals(1, summary.id)
        assertEquals("Test Article", summary.title)
        assertFalse(summary.isRead)
    }

    @Test
    fun `Summary copy works correctly`() {
        // Given
        val summary = MockDataFactory.createSummary(id = 1, isRead = false)

        // When
        val updatedSummary = summary.copy(isRead = true)

        // Then
        assertEquals(summary.id, updatedSummary.id)
        assertEquals(summary.title, updatedSummary.title)
        assertFalse(summary.isRead)
        assertTrue(updatedSummary.isRead)
    }

    @Test
    fun `Summary with topic tags is categorized correctly`() {
        // Given
        val techTags = listOf("technology", "ai", "machine-learning")
        val summary = MockDataFactory.createSummary(topicTags = techTags)

        // Then
        assertEquals(3, summary.topicTags.size)
        assertTrue(summary.topicTags.contains("technology"))
        assertTrue(summary.topicTags.contains("ai"))
    }

    @Test
    fun `Summary created date is in the past`() {
        // Given
        val summary = MockDataFactory.createSummary()

        // Then
        val now = Clock.System.now()
        assertTrue(summary.createdAt <= now)
    }

    @Test
    fun `Summary estimated reading time is positive`() {
        // Given
        val summary = MockDataFactory.createSummary(estimatedReadingTime = 5)

        // Then
        assertTrue(summary.estimatedReadingTime > 0)
    }

    @Test
    fun `Summary key points can be empty`() {
        // Given
        val summary = MockDataFactory.createSummary(keyPoints = emptyList())

        // Then
        assertTrue(summary.keyPoints.isEmpty())
    }

    @Test
    fun `Summary equality works correctly`() {
        // Given
        val summary1 = MockDataFactory.createSummary(id = 1)
        val summary2 = MockDataFactory.createSummary(id = 1)
        val summary3 = MockDataFactory.createSummary(id = 2)

        // Then
        assertEquals(summary1, summary2)
        assert(summary1 != summary3)
    }
}
