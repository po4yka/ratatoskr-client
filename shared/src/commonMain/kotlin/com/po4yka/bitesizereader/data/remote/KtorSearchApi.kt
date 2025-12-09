package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DuplicateUrlCheckResponseEnvelope
import com.po4yka.bitesizereader.data.remote.dto.RelatedSummariesResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto
import com.po4yka.bitesizereader.data.remote.dto.TrendingTopicsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorSearchApi(private val client: HttpClient) : SearchApi {
    override suspend fun checkDuplicateUrl(url: String): ApiResponseDto<DuplicateUrlCheckResponseEnvelope> {
        return client.get("v1/urls/check-duplicate") {
            parameter("url", url)
            parameter("include_summary", true)
        }.body()
    }

    override suspend fun search(
        query: String,
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<SearchResponseDataDto> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/search") {
            parameter("q", query)
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }

    override suspend fun semanticSearch(
        query: String,
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<SearchResponseDataDto> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/search/semantic") {
            parameter("q", query)
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }

    override suspend fun getTrendingTopics(
        limit: Int,
        days: Int,
    ): ApiResponseDto<TrendingTopicsResponseDto> {
        return client.get("v1/topics/trending") {
            parameter("limit", limit)
            parameter("days", days)
        }.body()
    }

    override suspend fun getRelatedSummaries(
        tag: String,
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<RelatedSummariesResponseDto> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/topics/related") {
            parameter("tag", tag)
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }
}
