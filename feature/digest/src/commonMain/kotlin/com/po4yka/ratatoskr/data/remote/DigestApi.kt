package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.CategoryResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ChannelListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestDeliveryListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestPreferenceResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.ratatoskr.data.remote.dto.SuccessResponse
import com.po4yka.ratatoskr.data.remote.dto.TriggerDigestResponseDto

/**
 * Digest API for channel subscriptions, preferences, history, and on-demand triggers.
 */
interface DigestApi {
    /** List user's channel subscriptions and slot usage. */
    suspend fun getChannels(): ApiResponseDto<ChannelListResponseDto>

    /** Subscribe to a Telegram channel for digest generation. */
    suspend fun subscribe(channelUsername: String): ApiResponseDto<ChannelListResponseDto>

    /** Unsubscribe from a Telegram channel. */
    suspend fun unsubscribe(channelUsername: String): ApiResponseDto<ChannelListResponseDto>

    /** Resolve channel details before subscribing. */
    suspend fun resolveChannel(channelUsername: String): ApiResponseDto<ResolveChannelResponseDto>

    /** Get merged digest preferences (user overrides plus global defaults). */
    suspend fun getPreferences(): ApiResponseDto<DigestPreferenceResponseDto>

    /** Update user digest preferences. */
    suspend fun updatePreferences(
        deliveryTime: String? = null,
        timezone: String? = null,
        hoursLookback: Int? = null,
        maxPostsPerDigest: Int? = null,
        minRelevanceScore: Double? = null,
    ): ApiResponseDto<DigestPreferenceResponseDto>

    /** Get paginated list of past digest deliveries. */
    suspend fun getHistory(
        page: Int = 1,
        pageSize: Int = 20,
    ): ApiResponseDto<DigestDeliveryListResponseDto>

    /** Trigger an on-demand digest generation for current user. */
    suspend fun triggerDigest(): ApiResponseDto<TriggerDigestResponseDto>

    /** Trigger digest for a specific channel. */
    suspend fun triggerChannel(channelUsername: String): ApiResponseDto<TriggerDigestResponseDto>

    /** Create a category for organizing channels. */
    suspend fun createCategory(name: String): ApiResponseDto<CategoryResponseDto>

    /** Bulk unsubscribe from multiple channels. */
    suspend fun bulkUnsubscribe(channelUsernames: List<String>): ApiResponseDto<SuccessResponse>
}
