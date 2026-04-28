package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubmitFeedbackRequestDto(
    val rating: String,
    val issues: List<String>,
    val comment: String?,
)
