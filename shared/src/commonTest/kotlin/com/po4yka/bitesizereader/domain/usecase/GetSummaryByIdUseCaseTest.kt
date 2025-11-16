package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import com.po4yka.bitesizereader.util.CoroutineTestBase
import com.po4yka.bitesizereader.util.MockDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
            coEvery { mockRepository.getSummaryById(summaryId) } returns flowOf(mockSummary)

            // When
            val result = useCase(summaryId).first()

            // Then
            assertEquals(mockSummary, result)
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId) }
        }

    @Test
    fun `invoke returns null when summary not found`() =
        runTest {
            // Given
            val summaryId = 999
            coEvery { mockRepository.getSummaryById(summaryId) } returns flowOf(null)

            // When
            val result = useCase(summaryId).first()

            // Then
            assertNull(result)
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId) }
        }

    @Test
    fun `invoke returns updated summary when summary changes`() =
        runTest {
            // Given
            val summaryId = 1
            val originalSummary = MockDataFactory.createSummary(id = summaryId, isRead = false)
            val updatedSummary = originalSummary.copy(isRead = true)
            coEvery { mockRepository.getSummaryById(summaryId) } returns flowOf(originalSummary, updatedSummary)

            // When
            val results = useCase(summaryId)

            // Then
            val firstResult = results.first()
            assertEquals(originalSummary, firstResult)
            coVerify(exactly = 1) { mockRepository.getSummaryById(summaryId) }
        }
}
