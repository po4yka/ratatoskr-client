package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.ChannelSubscriptionListData
import com.po4yka.ratatoskr.api.generated.models.DigestHistoryData
import com.po4yka.ratatoskr.api.generated.models.DigestPreferencesData
import com.po4yka.ratatoskr.domain.model.DigestChannel
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.domain.model.DigestHistoryStatus
import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo

fun ChannelSubscriptionListData.toDomain(): DigestSubscriptionInfo {
    val maxSlotsValue = maxChannels?.toInt() ?: 0
    return DigestSubscriptionInfo(
        channels =
            channels.map { ch ->
                DigestChannel(
                    username = ch.username,
                    subscribedAt = ch.createdAt.toString(),
                    isActive = ch.isActive,
                )
            },
        maxSlots = maxSlotsValue,
        usedSlots = activeCount.toInt(),
    )
}

fun DigestPreferencesData.toDomain(): DigestPreferences {
    return DigestPreferences(
        deliveryTime = deliveryTime,
        timezone = timezone,
        hoursLookback = hoursLookback.toInt(),
        maxPostsPerDigest = maxPostsPerDigest.toInt(),
        minRelevanceScore = minRelevanceScore,
    )
}

fun DigestHistoryData.toDomain(): List<DigestHistoryItem> {
    return deliveries.map { d ->
        DigestHistoryItem(
            id = d.id.toString(),
            deliveredAt = d.deliveredAt.toString(),
            channelCount = d.channelCount.toInt(),
            postCount = d.postCount.toInt(),
            // DigestDelivery has no status field; default to UNKNOWN
            status = DigestHistoryStatus.UNKNOWN,
        )
    }
}
