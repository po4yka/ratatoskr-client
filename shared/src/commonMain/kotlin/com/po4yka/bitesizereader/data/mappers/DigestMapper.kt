package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.domain.model.DigestChannel
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

fun JsonObject.toDigestSubscriptionInfo(): DigestSubscriptionInfo {
    val channelsArray = this["channels"] as? JsonArray ?: return DigestSubscriptionInfo()
    val channels =
        channelsArray.mapNotNull { element ->
            val obj = element as? JsonObject ?: return@mapNotNull null
            DigestChannel(
                username = obj["username"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null,
                subscribedAt = obj["subscribed_at"]?.jsonPrimitive?.contentOrNull,
                isActive = obj["is_active"]?.jsonPrimitive?.booleanOrNull ?: true,
            )
        }
    return DigestSubscriptionInfo(
        channels = channels,
        maxSlots = this["max_slots"]?.jsonPrimitive?.intOrNull ?: 0,
        usedSlots = this["used_slots"]?.jsonPrimitive?.intOrNull ?: channels.size,
    )
}

fun JsonObject.toDigestPreferences(): DigestPreferences {
    return DigestPreferences(
        deliveryTime = this["delivery_time"]?.jsonPrimitive?.contentOrNull ?: "08:00",
        timezone = this["timezone"]?.jsonPrimitive?.contentOrNull ?: "UTC",
        hoursLookback = this["hours_lookback"]?.jsonPrimitive?.intOrNull ?: 24,
        maxPostsPerDigest = this["max_posts_per_digest"]?.jsonPrimitive?.intOrNull ?: 10,
        minRelevanceScore = this["min_relevance_score"]?.jsonPrimitive?.doubleOrNull ?: 0.5,
    )
}

fun JsonObject.toDigestHistoryItems(): List<DigestHistoryItem> {
    val itemsArray =
        this["items"] as? JsonArray
            ?: this["deliveries"] as? JsonArray
            ?: return emptyList()

    return itemsArray.mapNotNull { element ->
        val obj = element as? JsonObject ?: return@mapNotNull null
        DigestHistoryItem(
            id =
                obj["id"]?.jsonPrimitive?.contentOrNull
                    ?: obj["digest_id"]?.jsonPrimitive?.contentOrNull
                    ?: return@mapNotNull null,
            deliveredAt =
                obj["delivered_at"]?.jsonPrimitive?.contentOrNull
                    ?: obj["created_at"]?.jsonPrimitive?.contentOrNull
                    ?: "",
            channelCount = obj["channel_count"]?.jsonPrimitive?.intOrNull ?: 0,
            postCount = obj["post_count"]?.jsonPrimitive?.intOrNull ?: 0,
            status = obj["status"]?.jsonPrimitive?.contentOrNull ?: "unknown",
        )
    }
}
