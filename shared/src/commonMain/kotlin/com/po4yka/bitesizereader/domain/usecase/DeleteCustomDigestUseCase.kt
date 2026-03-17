package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.CustomDigestRepository
import org.koin.core.annotation.Factory

@Factory
class DeleteCustomDigestUseCase(private val repository: CustomDigestRepository) {
    suspend operator fun invoke(id: String) = repository.deleteDigest(id)
}
