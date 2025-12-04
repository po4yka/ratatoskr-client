package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.RequestDto
import com.po4yka.bitesizereader.data.remote.dto.RequestStatusDto

interface RequestsApi {
    suspend fun submitUrl(url: String): RequestDto
    suspend fun getRequestStatus(id: String): RequestStatusDto
}
