package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.RssRepository
import org.koin.core.annotation.Factory

@Factory
class UnsubscribeFromFeedUseCase(private val rssRepository: RssRepository) {
    suspend operator fun invoke(subscriptionId: Int) = rssRepository.unsubscribe(subscriptionId)
}
