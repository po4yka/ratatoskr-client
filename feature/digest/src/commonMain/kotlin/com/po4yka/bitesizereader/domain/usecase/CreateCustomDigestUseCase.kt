package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.CustomDigest
import com.po4yka.bitesizereader.domain.model.DigestFormat
import com.po4yka.bitesizereader.domain.repository.CustomDigestRepository
import org.koin.core.annotation.Factory

@Factory
class CreateCustomDigestUseCase(private val repository: CustomDigestRepository) {
    suspend operator fun invoke(
        title: String,
        summaryIds: List<String>,
        format: DigestFormat,
    ): CustomDigest = repository.createDigest(title, summaryIds, format)
}
