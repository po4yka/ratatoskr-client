package com.po4yka.bitesizereader.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

/** Unit tests for the Request domain model */
class RequestTest {
    @Test
    fun `Request model has correct default values`() {
        // Given
        val now = Clock.System.now()
        val request =
            Request(
                id = "1",
                url = "https://example.com/article",
                status = RequestStatus.PENDING,
                createdAt = now,
                updatedAt = now,
            )

        // Then
        assertEquals("1", request.id)
        assertEquals("https://example.com/article", request.url)
        assertEquals(RequestStatus.PENDING, request.status)
    }

    @Test
    fun `Request status can be PROCESSING`() {
        // Given
        val now = Clock.System.now()
        val request =
            Request(
                id = "2",
                url = "https://example.com/processing",
                status = RequestStatus.PROCESSING,
                createdAt = now,
                updatedAt = now,
            )

        // Then
        assertEquals(RequestStatus.PROCESSING, request.status)
    }

    @Test
    fun `Request status can be COMPLETED`() {
        // Given
        val now = Clock.System.now()
        val request =
            Request(
                id = "3",
                url = "https://example.com/completed",
                status = RequestStatus.COMPLETED,
                createdAt = now,
                updatedAt = now,
            )

        // Then
        assertEquals(RequestStatus.COMPLETED, request.status)
    }

    @Test
    fun `Request status can be FAILED`() {
        // Given
        val now = Clock.System.now()
        val request =
            Request(
                id = "4",
                url = "https://example.com/failed",
                status = RequestStatus.FAILED,
                createdAt = now,
                updatedAt = now,
            )

        // Then
        assertEquals(RequestStatus.FAILED, request.status)
    }

    @Test
    fun `all RequestStatus values exist`() {
        // Then
        assertEquals(4, RequestStatus.entries.size)
        assertEquals(RequestStatus.PENDING, RequestStatus.valueOf("PENDING"))
        assertEquals(RequestStatus.PROCESSING, RequestStatus.valueOf("PROCESSING"))
        assertEquals(RequestStatus.COMPLETED, RequestStatus.valueOf("COMPLETED"))
        assertEquals(RequestStatus.FAILED, RequestStatus.valueOf("FAILED"))
    }
}
