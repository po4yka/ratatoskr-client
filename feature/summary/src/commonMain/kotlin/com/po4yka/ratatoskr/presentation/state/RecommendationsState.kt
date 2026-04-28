package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.Recommendation

data class RecommendationsState(
    val recommendations: List<Recommendation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
