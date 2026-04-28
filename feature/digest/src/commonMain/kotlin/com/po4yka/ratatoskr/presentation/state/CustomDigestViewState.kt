package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.CustomDigest

data class CustomDigestViewState(
    val digest: CustomDigest? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
