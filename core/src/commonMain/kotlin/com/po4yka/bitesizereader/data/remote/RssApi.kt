package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedItemsResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssOpmlImportResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssSubscribeRequestDto
import com.po4yka.bitesizereader.data.remote.dto.RssSubscribeResponseDto

interface RssApi {
    suspend fun listFeeds(): ApiResponseDto<RssFeedListResponseDto>

    suspend fun subscribe(request: RssSubscribeRequestDto): ApiResponseDto<RssSubscribeResponseDto>

    suspend fun unsubscribe(subscriptionId: Int): ApiResponseDto<RssDeleteResponseDto>

    suspend fun listFeedItems(
        feedId: Int,
        limit: Int = 20,
        offset: Int = 0,
    ): ApiResponseDto<RssFeedItemsResponseDto>

    suspend fun refreshFeed(feedId: Int): ApiResponseDto<RssFeedRefreshResponseDto>

    suspend fun exportOpml(): ByteArray

    suspend fun importOpml(fileBytes: ByteArray): ApiResponseDto<RssOpmlImportResponseDto>
}
