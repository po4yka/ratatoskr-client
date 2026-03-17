package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Collection

data class CollectionDialogState(
    val showDialog: Boolean = false,
    val collections: List<Collection> = emptyList(),
    val isLoading: Boolean = false,
    val isAdding: Boolean = false,
    val error: String? = null,
)
