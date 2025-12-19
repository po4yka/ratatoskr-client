package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkBeginResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkCompleteRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLinkStatusDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.koin.core.annotation.Single

@Single(binds = [UserApi::class])
class KtorUserApi(
    private val client: HttpClient,
) : UserApi {
    override suspend fun getTelegramLinkStatus(): ApiResponseDto<TelegramLinkStatusDto> {
        return client.get("v1/me/telegram").body()
    }

    override suspend fun unlinkTelegram(): ApiResponseDto<TelegramLinkStatusDto> {
        return client.delete("v1/me/telegram").body()
    }

    override suspend fun beginTelegramLink(): ApiResponseDto<TelegramLinkBeginResponseDto> {
        return client.post("v1/me/telegram/link").body()
    }

    override suspend fun completeTelegramLink(
        request: TelegramLinkCompleteRequestDto,
    ): ApiResponseDto<TelegramLinkStatusDto> {
        return client.post("v1/me/telegram/complete") {
            setBody(request)
        }.body()
    }
}
