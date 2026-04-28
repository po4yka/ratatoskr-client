package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.Request
import com.po4yka.ratatoskr.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetRequestsUseCase(private val repository: RequestRepository) {
    operator fun invoke(): Flow<List<Request>> {
        return repository.getRequests()
    }
}
