package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDto

interface SearchApi {
    suspend fun search(query: String, page: Int, pageSize: Int): SearchResponseDto
}
