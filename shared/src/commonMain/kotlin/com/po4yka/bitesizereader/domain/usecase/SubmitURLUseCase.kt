package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository

/**
 * Use case for submitting a URL for summarization
 */
class SubmitURLUseCase(
    private val requestRepository: RequestRepository
) {
    suspend operator fun invoke(
        url: String,
        langPreference: String = "auto"
    ): Result<Request> {
        // Validate URL
        if (!isValidURL(url)) {
            return Result.failure(IllegalArgumentException("Invalid URL"))
        }

        return requestRepository.submitURL(url, langPreference)
    }

    private fun isValidURL(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }
}
