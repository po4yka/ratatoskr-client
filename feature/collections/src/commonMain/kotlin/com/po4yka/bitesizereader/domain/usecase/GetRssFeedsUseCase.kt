package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.model.RssFeedSubscription
import com.po4yka.bitesizereader.domain.repository.RssRepository
import org.koin.core.annotation.Factory

@Factory
class GetRssFeedsUseCase(private val rssRepository: RssRepository) {
    suspend operator fun invoke(): List<RssFeedSubscription> = rssRepository.listFeeds()
}
