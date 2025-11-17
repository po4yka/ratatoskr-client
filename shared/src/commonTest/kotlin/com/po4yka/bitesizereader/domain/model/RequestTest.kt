package com.po4yka.bitesizereader.domain.model

import com.po4yka.bitesizereader.util.MockDataFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for Request domain model
 */
class RequestTest {
    @Test
    fun `Request is created with correct properties`() {
        // Given
        val request =
            MockDataFactory.createRequest(
                id = 1,
                inputUrl = "https://example.com/article",
                status = RequestStatus.PENDING,
            )

        // Then
        assertEquals(1, request.id)
        assertEquals("https://example.com/article", request.inputUrl)
        assertEquals(RequestStatus.PENDING, request.status)
    }

    @Test
    fun `Request status can transition from PENDING to PROCESSING`() {
        // Given
        val request = MockDataFactory.createRequest(status = RequestStatus.PENDING)

        // When
        val updatedRequest = request.copy(status = RequestStatus.PROCESSING)

        // Then
        assertEquals(RequestStatus.PENDING, request.status)
        assertEquals(RequestStatus.PROCESSING, updatedRequest.status)
    }

    @Test
    fun `Request status can transition to COMPLETED with summaryId`() {
        // Given
        val request = MockDataFactory.createRequest(status = RequestStatus.PROCESSING)

        // When
        val updatedRequest =
            request.copy(
                status = RequestStatus.COMPLETED,
                summaryId = 123,
            )

        // Then
        assertEquals(RequestStatus.COMPLETED, updatedRequest.status)
        assertEquals(123, updatedRequest.summaryId)
    }

    @Test
    fun `Request status can transition to ERROR with error message`() {
        // Given
        val request = MockDataFactory.createRequest(status = RequestStatus.PROCESSING)

        // When
        val updatedRequest =
            request.copy(
                status = RequestStatus.ERROR,
                errorMessage = "Failed to fetch article",
            )

        // Then
        assertEquals(RequestStatus.ERROR, updatedRequest.status)
        assertEquals("Failed to fetch article", updatedRequest.errorMessage)
    }

    @Test
    fun `Request without summaryId has null value`() {
        // Given
        val request = MockDataFactory.createRequest(summaryId = null)

        // Then
        assertNull(request.summaryId)
    }

    @Test
    fun `Request without errorMessage has null value`() {
        // Given
        val request = MockDataFactory.createRequest(errorMessage = null)

        // Then
        assertNull(request.errorMessage)
    }

    @Test
    fun `Request type can be URL or YOUTUBE_VIDEO`() {
        // Given
        val urlRequest = MockDataFactory.createRequest(id = 1, type = RequestType.URL)
        val videoRequest = MockDataFactory.createRequest(id = 2, type = RequestType.YOUTUBE_VIDEO)

        // Then
        assertEquals(RequestType.URL, urlRequest.type)
        assertEquals(RequestType.YOUTUBE_VIDEO, videoRequest.type)
    }
}
