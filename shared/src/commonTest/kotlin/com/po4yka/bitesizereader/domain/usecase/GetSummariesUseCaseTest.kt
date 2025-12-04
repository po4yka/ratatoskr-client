package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSummariesUseCaseTest {

    private val mockRepository = object : SummaryRepository {
        override fun getSummaries(page: Int, pageSize: Int, tags: List<String>?) = flowOf(
            listOf(
                Summary(
                    id = "1",
                    title = "Test Summary",
                    content = "Content",
                    sourceUrl = "url",
                    imageUrl = null,
                    createdAt = Clock.System.now(),
                    isRead = false,
                    tags = emptyList()
                )
            )
        )

        override suspend fun getSummaryById(id: String): Summary? = null
        override suspend fun markAsRead(id: String) {}
        override suspend fun deleteSummary(id: String) {}
    }

    private val useCase = GetSummariesUseCase(mockRepository)

    @Test
    fun `invoke returns summaries from repository`() = runTest {
        val result = useCase(1, 20)
        result.collect { summaries ->
            assertEquals(1, summaries.size)
            assertEquals("Test Summary", summaries[0].title)
        }
    }
}
