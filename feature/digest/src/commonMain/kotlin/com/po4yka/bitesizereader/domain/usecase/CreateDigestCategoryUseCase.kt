package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class CreateDigestCategoryUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(name: String): String {
        return repository.createCategory(name)
    }
}
