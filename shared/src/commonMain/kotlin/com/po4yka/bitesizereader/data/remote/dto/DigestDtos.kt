package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Request body for subscribe/unsubscribe channel operations.
 */
@Serializable
data class DigestChannelRequestDto(
    @SerialName("channel_username") val channelUsername: String,
)

/**
 * Request body for updating digest preferences.
 */
@Serializable
data class UpdateDigestPreferencesRequestDto(
    @SerialName("delivery_time") val deliveryTime: String? = null,
    @SerialName("timezone") val timezone: String? = null,
    @SerialName("hours_lookback") val hoursLookback: Int? = null,
    @SerialName("max_posts_per_digest") val maxPostsPerDigest: Int? = null,
    @SerialName("min_relevance_score") val minRelevanceScore: Double? = null,
)

/**
 * Digest endpoint responses are untyped objects in the OpenAPI spec.
 * We use JsonObject to preserve full fidelity without making assumptions.
 */
typealias DigestResponseData = JsonObject
