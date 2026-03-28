package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.RssRepository
import org.koin.core.annotation.Factory

@Factory
class UnsubscribeFromFeedUseCase(private val rssRepository: RssRepository) {
    suspend operator fun invoke(subscriptionId: Int) = rssRepository.unsubscribe(subscriptionId)
}
