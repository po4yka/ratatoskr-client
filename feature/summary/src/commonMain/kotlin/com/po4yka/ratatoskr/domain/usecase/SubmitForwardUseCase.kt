package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.repository.RequestRepository
import org.koin.core.annotation.Factory

@Factory
class SubmitForwardUseCase(private val repository: RequestRepository) {
    suspend operator fun invoke(
        contentText: String,
        langPreference: String = "auto",
    ): Request {
        return repository.submitForward(contentText, langPreference)
    }
}
