package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// -- Request DTOs --

@Serializable
data class DigestChannelRequestDto(
    @SerialName("channel_username") val channelUsername: String,
)

@Serializable
data class UpdateDigestPreferencesRequestDto(
    @SerialName("delivery_time") val deliveryTime: String? = null,
    @SerialName("timezone") val timezone: String? = null,
    @SerialName("hours_lookback") val hoursLookback: Int? = null,
    @SerialName("max_posts_per_digest") val maxPostsPerDigest: Int? = null,
    @SerialName("min_relevance_score") val minRelevanceScore: Double? = null,
)

@Serializable
data class ResolveChannelRequestDto(
    @SerialName("channel_username") val channelUsername: String,
)

@Serializable
data class CreateCategoryRequestDto(
    @SerialName("name") val name: String,
)

@Serializable
data class BulkUnsubscribeRequestDto(
    @SerialName("channel_usernames") val channelUsernames: List<String>,
)

@Serializable
data class TriggerChannelRequestDto(
    @SerialName("channel_username") val channelUsername: String,
)

// -- Response DTOs --

@Serializable
data class ChannelListResponseDto(
    @SerialName("channels") val channels: List<ChannelDto> = emptyList(),
    @SerialName("max_slots") val maxSlots: Int = 0,
    @SerialName("used_slots") val usedSlots: Int = 0,
)

@Serializable
data class ChannelDto(
    @SerialName("username") val username: String,
    @SerialName("title") val title: String? = null,
    @SerialName("subscribed_at") val subscribedAt: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class ResolveChannelResponseDto(
    @SerialName("username") val username: String,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("subscriber_count") val subscriberCount: Int? = null,
)

@Serializable
data class DigestPreferenceResponseDto(
    @SerialName("delivery_time") val deliveryTime: String = "08:00",
    @SerialName("timezone") val timezone: String = "UTC",
    @SerialName("hours_lookback") val hoursLookback: Int = 24,
    @SerialName("max_posts_per_digest") val maxPostsPerDigest: Int = 10,
    @SerialName("min_relevance_score") val minRelevanceScore: Double = 0.5,
)

@Serializable
data class DigestDeliveryListResponseDto(
    @SerialName("deliveries") val deliveries: List<DigestDeliveryDto> = emptyList(),
)

@Serializable
data class DigestDeliveryDto(
    @SerialName("id") val id: String,
    @SerialName("delivered_at") val deliveredAt: String,
    @SerialName("channel_count") val channelCount: Int = 0,
    @SerialName("post_count") val postCount: Int = 0,
    @SerialName("status") val status: String = "unknown",
)

@Serializable
data class TriggerDigestResponseDto(
    @SerialName("status") val status: String,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class CategoryResponseDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("post_count") val postCount: Int? = null,
)
