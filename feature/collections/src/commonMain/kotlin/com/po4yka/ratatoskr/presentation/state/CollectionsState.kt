package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.Collection

data class CollectionsState(
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val isCreating: Boolean = false,
    val createError: String? = null,
)
