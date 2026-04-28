package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.RssFeedSubscription
import com.po4yka.ratatoskr.domain.repository.RssRepository
import org.koin.core.annotation.Factory

@Factory
class SubscribeToFeedUseCase(private val rssRepository: RssRepository) {
    suspend operator fun invoke(
        url: String,
        categoryId: Int? = null,
    ): RssFeedSubscription = rssRepository.subscribe(url = url, categoryId = categoryId)
}
