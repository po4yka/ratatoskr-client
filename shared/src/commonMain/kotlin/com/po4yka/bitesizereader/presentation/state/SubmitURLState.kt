package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Request

/**
 * UI state for submit URL screen
 */
data class SubmitURLState(
    val url: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val request: Request? = null,
    val isPolling: Boolean = false
)
