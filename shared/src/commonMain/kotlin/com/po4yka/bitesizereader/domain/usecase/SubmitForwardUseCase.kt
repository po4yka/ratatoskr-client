package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository
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
