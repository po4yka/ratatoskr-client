package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.DigestTriggerResult
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class TriggerDigestUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): Boolean =
        when (val result = repository.triggerDigest()) {
            is DigestTriggerResult.Triggered -> result.status != "failed"
            DigestTriggerResult.NoServerResponse -> false
        }
}
