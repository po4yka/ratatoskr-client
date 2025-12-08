package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.RelatedSummariesResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto
import com.po4yka.bitesizereader.data.remote.dto.TrendingTopicsResponseDto

interface SearchApi {
    suspend fun search(query: String, page: Int, pageSize: Int): ApiResponseDto<SearchResponseDataDto>
    suspend fun semanticSearch(query: String, page: Int, pageSize: Int): ApiResponseDto<SearchResponseDataDto>
    suspend fun getTrendingTopics(limit: Int, days: Int): ApiResponseDto<TrendingTopicsResponseDto>
    suspend fun getRelatedSummaries(tag: String, page: Int, pageSize: Int): ApiResponseDto<RelatedSummariesResponseDto>
}
