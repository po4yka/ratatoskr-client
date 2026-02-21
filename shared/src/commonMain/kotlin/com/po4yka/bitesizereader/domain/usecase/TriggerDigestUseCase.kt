package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.DigestRepository
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.annotation.Factory

@Factory
class TriggerDigestUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(): Boolean {
        val result = repository.triggerDigest()
        return result["success"]?.jsonPrimitive?.booleanOrNull
            ?: result["triggered"]?.jsonPrimitive?.booleanOrNull
            ?: true
    }
}
