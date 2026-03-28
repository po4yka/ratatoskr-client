package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import org.koin.core.annotation.Factory

@Factory
class RetryRequestUseCase(private val repository: RequestRepository) {
    suspend operator fun invoke(request: Request): Request {
        // Retry logic same as submit usually
        return repository.submitUrl(request.url)
    }
}
