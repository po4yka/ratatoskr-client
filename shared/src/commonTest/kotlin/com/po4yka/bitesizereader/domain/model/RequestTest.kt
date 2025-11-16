package com.po4yka.bitesizereader.domain.model

import com.po4yka.bitesizereader.util.MockDataFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for Request domain model
 */
class RequestTest {

    @Test
    fun `Request is created with correct properties`() {
        // Given
        val request = MockDataFactory.createRequest(
            id = 1,
            url = "https://example.com/article",
            status = RequestStatus.PENDING
        )

        // Then
        assertEquals(1, request.id)
        assertEquals("https://example.com/article", request.url)
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
        val updatedRequest = request.copy(
            status = RequestStatus.COMPLETED,
            summaryId = 123
        )

        // Then
        assertEquals(RequestStatus.COMPLETED, updatedRequest.status)
        assertEquals(123, updatedRequest.summaryId)
    }

    @Test
    fun `Request status can transition to FAILED with error message`() {
        // Given
        val request = MockDataFactory.createRequest(status = RequestStatus.PROCESSING)

        // When
        val updatedRequest = request.copy(
            status = RequestStatus.FAILED,
            error = "Failed to fetch article"
        )

        // Then
        assertEquals(RequestStatus.FAILED, updatedRequest.status)
        assertEquals("Failed to fetch article", updatedRequest.error)
    }

    @Test
    fun `Request without summaryId has null value`() {
        // Given
        val request = MockDataFactory.createRequest(summaryId = null)

        // Then
        assertNull(request.summaryId)
    }

    @Test
    fun `Request without error has null value`() {
        // Given
        val request = MockDataFactory.createRequest(error = null)

        // Then
        assertNull(request.error)
    }

    @Test
    fun `Request clientId is unique identifier`() {
        // Given
        val request1 = MockDataFactory.createRequest(id = 1, clientId = "client-1")
        val request2 = MockDataFactory.createRequest(id = 2, clientId = "client-2")

        // Then
        assert(request1.clientId != request2.clientId)
    }
}
