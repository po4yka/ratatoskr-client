package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssDeleteResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedItemsResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssFeedRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssOpmlImportResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RssSubscribeRequestDto
import com.po4yka.bitesizereader.data.remote.dto.RssSubscribeResponseDto
import com.po4yka.bitesizereader.util.retry.RetryPolicy
import com.po4yka.bitesizereader.util.retry.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [RssApi::class])
class KtorRssApi(private val client: HttpClient) : RssApi {
    override suspend fun listFeeds(): ApiResponseDto<RssFeedListResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/rss/feeds").body()
        }

    override suspend fun subscribe(request: RssSubscribeRequestDto): ApiResponseDto<RssSubscribeResponseDto> {
        return client.post("v1/rss/feeds/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun unsubscribe(subscriptionId: Int): ApiResponseDto<RssDeleteResponseDto> {
        return client.delete("v1/rss/feeds/$subscriptionId").body()
    }

    override suspend fun listFeedItems(
        feedId: Int,
        limit: Int,
        offset: Int,
    ): ApiResponseDto<RssFeedItemsResponseDto> =
        retryWithBackoff(RetryPolicy.DEFAULT) {
            client.get("v1/rss/feeds/$feedId/items") {
                parameter("limit", limit)
                parameter("offset", offset)
            }.body()
        }

    override suspend fun refreshFeed(feedId: Int): ApiResponseDto<RssFeedRefreshResponseDto> {
        return client.post("v1/rss/feeds/$feedId/refresh").body()
    }

    override suspend fun exportOpml(): ByteArray {
        return client.get("v1/rss/export/opml").bodyAsBytes()
    }

    override suspend fun importOpml(fileBytes: ByteArray): ApiResponseDto<RssOpmlImportResponseDto> {
        return client.submitFormWithBinaryData(
            url = "v1/rss/import/opml",
            formData =
                formData {
                    append(
                        "file",
                        fileBytes,
                        Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"subscriptions.opml\"")
                        },
                    )
                },
        ).body()
    }
}
