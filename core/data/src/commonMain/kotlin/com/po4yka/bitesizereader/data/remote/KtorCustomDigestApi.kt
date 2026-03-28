package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CreateCustomDigestRequestDto
import com.po4yka.bitesizereader.data.remote.dto.CustomDigestListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CustomDigestResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [CustomDigestApi::class])
class KtorCustomDigestApi(private val client: HttpClient) : CustomDigestApi {
    override suspend fun createCustomDigest(
        request: CreateCustomDigestRequestDto,
    ): ApiResponseDto<CustomDigestResponseDto> =
        client.post("v1/digests/custom") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun getCustomDigest(id: String): ApiResponseDto<CustomDigestResponseDto> =
        client.get("v1/digests/custom/$id").body()

    override suspend fun getCustomDigests(
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<CustomDigestListResponseDto> =
        client.get("v1/digests/custom") {
            parameter("page", page)
            parameter("page_size", pageSize)
        }.body()
}
