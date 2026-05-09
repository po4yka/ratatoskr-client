package com.po4yka.ratatoskr.domain.model

data class DigestChannel(
    val username: String,
    val subscribedAt: String? = null,
    val isActive: Boolean = true,
)

data class DigestSubscriptionInfo(
    val channels: List<DigestChannel> = emptyList(),
    val maxSlots: Int = 0,
    val usedSlots: Int = 0,
)

data class DigestPreferences(
    val deliveryTime: String = "08:00",
    val timezone: String = "UTC",
    val hoursLookback: Int = 24,
    val maxPostsPerDigest: Int = 10,
    val minRelevanceScore: Double = 0.5,
)

enum class DigestHistoryStatus {
    PENDING, DELIVERED, COMPLETED, FAILED, UNKNOWN;

    companion object {
        fun fromString(value: String): DigestHistoryStatus =
            when (value.lowercase()) {
                "pending" -> PENDING
                "delivered" -> DELIVERED
                "completed" -> COMPLETED
                "failed" -> FAILED
                else -> UNKNOWN
            }
    }
}

data class DigestHistoryItem(
    val id: String,
    val deliveredAt: String,
    val channelCount: Int = 0,
    val postCount: Int = 0,
    val status: DigestHistoryStatus = DigestHistoryStatus.UNKNOWN,
)

sealed class DigestTriggerResult {
    data class Triggered(val status: String, val message: String? = null) : DigestTriggerResult()
    data object NoServerResponse : DigestTriggerResult()
}
