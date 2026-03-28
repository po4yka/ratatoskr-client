package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.ChannelListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DigestDeliveryListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DigestPreferenceResponseDto
import com.po4yka.bitesizereader.domain.model.DigestChannel
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo

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
