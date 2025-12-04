package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class KtorAuthApi(private val client: HttpClient) : AuthApi {
    override suspend fun login(authRequest: AuthRequestDto): AuthResponseDto {
        return client.post("auth/telegram") {
            setBody(authRequest)
        }.body()
    }

    override suspend fun refreshToken(refreshToken: String): AuthResponseDto {
        // Usually refresh token is handled by Auth plugin, but if exposed manually:
        return client.post("auth/refresh") {
            setBody(mapOf("refresh_token" to refreshToken))
        }.body()
    }
}
