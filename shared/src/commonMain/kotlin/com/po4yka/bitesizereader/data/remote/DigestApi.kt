package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DigestResponseData

/**
 * Digest API for channel subscriptions, preferences, history, and on-demand triggers.
 */
interface DigestApi {
    /** List user's channel subscriptions and slot usage. */
    suspend fun getChannels(): ApiResponseDto<DigestResponseData>

    /** Subscribe to a Telegram channel for digest generation. */
    suspend fun subscribe(channelUsername: String): ApiResponseDto<DigestResponseData>

    /** Unsubscribe from a Telegram channel. */
    suspend fun unsubscribe(channelUsername: String): ApiResponseDto<DigestResponseData>

    /** Get merged digest preferences (user overrides plus global defaults). */
    suspend fun getPreferences(): ApiResponseDto<DigestResponseData>

    /** Update user digest preferences. */
    suspend fun updatePreferences(
        deliveryTime: String? = null,
        timezone: String? = null,
        hoursLookback: Int? = null,
        maxPostsPerDigest: Int? = null,
        minRelevanceScore: Double? = null,
    ): ApiResponseDto<DigestResponseData>

    /** Get paginated list of past digest deliveries. */
    suspend fun getHistory(
        page: Int = 1,
        pageSize: Int = 20,
    ): ApiResponseDto<DigestResponseData>

    /** Trigger an on-demand digest generation for current user. */
    suspend fun triggerDigest(): ApiResponseDto<DigestResponseData>
}
