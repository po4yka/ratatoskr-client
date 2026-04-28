package com.po4yka.ratatoskr.presentation.state

import com.po4yka.ratatoskr.domain.model.Collection

data class CollectionDialogState(
    val showDialog: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val isAdding: Boolean = false,
    val error: String? = null,
)
