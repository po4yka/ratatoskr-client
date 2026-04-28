package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.ChannelListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestDeliveryListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestPreferenceResponseDto
import com.po4yka.ratatoskr.domain.model.DigestChannel
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo

fun ChannelListResponseDto.toDomain(): DigestSubscriptionInfo {
    return DigestSubscriptionInfo(
        channels =
            channels.map { ch ->
                DigestChannel(
                    username = ch.username,
                    subscribedAt = ch.subscribedAt,
                    isActive = ch.isActive,
                )
            },
        maxSlots = maxSlots,
        usedSlots = usedSlots,
    )
}

fun DigestPreferenceResponseDto.toDomain(): DigestPreferences {
    return DigestPreferences(
        deliveryTime = deliveryTime,
        timezone = timezone,
        hoursLookback = hoursLookback,
        maxPostsPerDigest = maxPostsPerDigest,
        minRelevanceScore = minRelevanceScore,
    )
}

fun DigestDeliveryListResponseDto.toDomain(): List<DigestHistoryItem> {
    return deliveries.map { d ->
        DigestHistoryItem(
            id = d.id,
            deliveredAt = d.deliveredAt,
            channelCount = d.channelCount,
            postCount = d.postCount,
            status = d.status,
        )
    }
}
