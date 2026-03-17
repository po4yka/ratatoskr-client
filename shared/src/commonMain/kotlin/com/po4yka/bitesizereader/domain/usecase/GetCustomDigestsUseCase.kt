package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.CustomDigest
import com.po4yka.bitesizereader.domain.repository.CustomDigestRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetCustomDigestsUseCase(private val repository: CustomDigestRepository) {
    operator fun invoke(): Flow<List<CustomDigest>> = repository.getDigests()
}
