package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository

class SubmitURLUseCase(private val repository: RequestRepository) {
    suspend operator fun invoke(url: String): Request {
        return repository.submitUrl(url)
    }
}