package com.po4yka.ratatoskr.domain.model

data class ResolvedChannel(
    val username: String,
    val title: String? = null,
    val description: String? = null,
    val subscriberCount: Int? = null,
)
