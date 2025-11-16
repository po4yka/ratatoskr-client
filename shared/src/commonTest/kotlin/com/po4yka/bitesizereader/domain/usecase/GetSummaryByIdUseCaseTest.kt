package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.util.CoroutineTestBase
import com.po4yka.bitesizereader.util.MockDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for GetSummaryByIdUseCase
 */
class GetSummaryByIdUseCaseTest : CoroutineTestBase() {
    private val mockRepository = mockk<SummaryRepository>()
    private val useCase = GetSummaryByIdUseCase(mockRepository)

    @Test
    fun `invoke returns summary when found`() =
        runTest {
            // Given
            val summaryId = 1
            val mockSummary = MockDataFactory.createSummary(id = summaryId)
            coEvery { mockRepository.getSummaryById(summaryId) } returns Result.success(mockSummary)

            // When
            val result = useCase(summaryId)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(mockSummary, result.getOrNull())
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId) }
        }

    @Test
    fun `invoke returns failure when summary not found`() =
        runTest {
            // Given
            val summaryId = 999
            val error = Exception("Summary not found")
            coEvery { mockRepository.getSummaryById(summaryId) } returns Result.failure(error)

            // When
            val result = useCase(summaryId)

            // Then
            assertTrue(result.isFailure)
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId) }
        }

    @Test
    fun `invoke returns correct summary for different IDs`() =
        runTest {
            // Given
            val summaryId1 = 1
            val summaryId2 = 2
            val mockSummary1 = MockDataFactory.createSummary(id = summaryId1, isRead = false)
            val mockSummary2 = MockDataFactory.createSummary(id = summaryId2, isRead = true)
            coEvery { mockRepository.getSummaryById(summaryId1) } returns Result.success(mockSummary1)
            coEvery { mockRepository.getSummaryById(summaryId2) } returns Result.success(mockSummary2)

            // When
            val result1 = useCase(summaryId1)
            val result2 = useCase(summaryId2)

            // Then
            assertEquals(mockSummary1, result1.getOrNull())
            assertEquals(mockSummary2, result2.getOrNull())
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId1) }
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId2) }
        }
}
