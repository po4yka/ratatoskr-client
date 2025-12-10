package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.repository.RequestRepository
import org.koin.core.annotation.Factory

@Factory
class GetRequestStatusUseCase(private val repository: RequestRepository) {
    suspend operator fun invoke(id: String): Request {
        return repository.getRequestStatus(id)
    }
}
