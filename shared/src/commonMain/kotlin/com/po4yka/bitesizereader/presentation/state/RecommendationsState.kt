package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Recommendation

data class RecommendationsState(
    val recommendations: List<Recommendation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
