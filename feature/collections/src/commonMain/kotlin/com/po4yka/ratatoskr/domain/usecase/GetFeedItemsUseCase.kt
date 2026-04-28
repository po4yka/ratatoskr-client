package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.model.RssFeedItem
import com.po4yka.ratatoskr.domain.repository.RssRepository
import org.koin.core.annotation.Factory

@Factory
class GetFeedItemsUseCase(private val rssRepository: RssRepository) {
    suspend operator fun invoke(
        feedId: Int,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RssFeedItem> = rssRepository.listFeedItems(feedId = feedId, limit = limit, offset = offset)
}
