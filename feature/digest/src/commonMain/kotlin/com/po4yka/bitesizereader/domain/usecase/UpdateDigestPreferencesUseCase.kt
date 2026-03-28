package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Factory

@Factory
class UpdateDigestPreferencesUseCase(private val repository: DigestRepository) {
    suspend operator fun invoke(
        deliveryTime: String? = null,
        timezone: String? = null,
        hoursLookback: Int? = null,
        maxPostsPerDigest: Int? = null,
        minRelevanceScore: Double? = null,
    ): DigestPreferences {
        return repository.updatePreferences(
            deliveryTime = deliveryTime,
            timezone = timezone,
            hoursLookback = hoursLookback,
            maxPostsPerDigest = maxPostsPerDigest,
            minRelevanceScore = minRelevanceScore,
        )
    }
}
