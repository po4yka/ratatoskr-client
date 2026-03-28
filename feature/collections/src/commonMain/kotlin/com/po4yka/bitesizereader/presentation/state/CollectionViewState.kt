package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.Summary

data class CollectionHeaderState(
    val collection: Collection? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSystemCollection: Boolean = false,
    val canEdit: Boolean = false,
    val canShare: Boolean = false,
)

data class CollectionItemsState(
    val items: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = true,
)

data class CollectionSettingsState(
    val isUpdating: Boolean = false,
    val updateError: String? = null,
    val updateSuccess: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
)

data class CollectionSharingState(
    val collaborators: List<CollectionAcl> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inviteLink: CollectionInvite? = null,
    val isCreatingInvite: Boolean = false,
    val inviteError: String? = null,
)

data class CollectionViewState(
    val selectedTab: CollectionViewTab = CollectionViewTab.Items,
    val header: CollectionHeaderState = CollectionHeaderState(),
    val items: CollectionItemsState = CollectionItemsState(),
    val settings: CollectionSettingsState = CollectionSettingsState(),
    val sharing: CollectionSharingState = CollectionSharingState(),
)

enum class CollectionViewTab {
    Items,
    Settings,
    Sharing,
}
