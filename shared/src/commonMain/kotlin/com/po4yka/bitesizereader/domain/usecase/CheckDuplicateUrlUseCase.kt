package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.SearchApi
import org.koin.core.annotation.Factory

data class DuplicateCheckResult(
    val isDuplicate: Boolean,
    val existingSummaryId: String?,
)

@Factory
class CheckDuplicateUrlUseCase(private val searchApi: SearchApi) {
    suspend operator fun invoke(url: String): DuplicateCheckResult {
        val response = searchApi.checkDuplicateUrl(url, includeSummary = false)
        val data = response.data
        return if (response.success && data != null) {
            DuplicateCheckResult(
                isDuplicate = data.data.isDuplicate,
                existingSummaryId = data.data.summaryId?.toString(),
            )
        } else {
            DuplicateCheckResult(isDuplicate = false, existingSummaryId = null)
        }
    }
}
