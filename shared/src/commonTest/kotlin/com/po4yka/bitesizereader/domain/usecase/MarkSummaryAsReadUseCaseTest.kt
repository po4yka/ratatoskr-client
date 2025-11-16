package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.util.CoroutineTestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for MarkSummaryAsReadUseCase
 */
class MarkSummaryAsReadUseCaseTest : CoroutineTestBase() {
    private val mockRepository = mockk<SummaryRepository>()
    private val useCase = MarkSummaryAsReadUseCase(mockRepository)

    @Test
    fun `invoke marks summary as read successfully`() =
        runTest {
            // Given
            val summaryId = 1
            val isRead = true
            coEvery { mockRepository.markAsRead(summaryId, isRead) } returns Result.success(Unit)

            // When
            val result = useCase(summaryId, isRead)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { mockRepository.markAsRead(summaryId, isRead) }
        }

    @Test
    fun `invoke marks summary as unread successfully`() =
        runTest {
            // Given
            val summaryId = 1
            val isRead = false
            coEvery { mockRepository.markAsRead(summaryId, isRead) } returns Result.success(Unit)

            // When
            val result = useCase(summaryId, isRead)

            // Then
            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { mockRepository.markAsRead(summaryId, isRead) }
        }

    @Test
    fun `invoke returns failure when repository fails`() =
        runTest {
            // Given
            val summaryId = 1
            val isRead = true
            val error = Exception("Failed to update summary")
            coEvery { mockRepository.markAsRead(summaryId, isRead) } returns Result.failure(error)

            // When
            val result = useCase(summaryId, isRead)

            // Then
            assertTrue(result.isFailure)
            coVerify(exactly = 1) { mockRepository.markAsRead(summaryId, isRead) }
        }
}
