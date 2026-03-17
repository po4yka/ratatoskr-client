package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.CustomDigest
import com.po4yka.bitesizereader.domain.repository.CustomDigestRepository
import org.koin.core.annotation.Factory

@Factory
class GetCustomDigestByIdUseCase(private val repository: CustomDigestRepository) {
    suspend operator fun invoke(id: String): CustomDigest? = repository.getDigest(id)
}
