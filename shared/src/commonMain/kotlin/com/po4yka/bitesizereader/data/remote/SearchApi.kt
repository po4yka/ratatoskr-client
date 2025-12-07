package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SearchResponseDataDto

interface SearchApi {
    suspend fun search(query: String, page: Int, pageSize: Int): ApiResponseDto<SearchResponseDataDto>
}
