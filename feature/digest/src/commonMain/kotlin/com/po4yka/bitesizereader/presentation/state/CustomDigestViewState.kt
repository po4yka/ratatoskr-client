package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.CustomDigest

data class CustomDigestViewState(
    val digest: CustomDigest? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
