package com.po4yka.ratatoskr.domain.model

data class Recommendation(
    val id: String,
    val summary: Summary,
    val score: Double,
    val reason: String?,
    val strategy: RecommendationStrategy,
)

enum class RecommendationStrategy(val value: String) {
    PERSONALIZED("personalized"),
    SIMILAR("similar"),
    TRENDING("trending"),
}
