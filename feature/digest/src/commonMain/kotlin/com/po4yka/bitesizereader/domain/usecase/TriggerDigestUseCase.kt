package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class TriggerDigestUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): Boolean {
        val status = repository.triggerDigest()
        return status != "failed"
    }
}
