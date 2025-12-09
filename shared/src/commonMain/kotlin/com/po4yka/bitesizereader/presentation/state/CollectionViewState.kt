package com.po4yka.bitesizereader.presentation.state

import com.po4yka.bitesizereader.domain.model.Collection
import com.po4yka.bitesizereader.domain.model.CollectionAcl
import com.po4yka.bitesizereader.domain.model.CollectionInvite
import com.po4yka.bitesizereader.domain.model.Summary

data class CollectionViewState(
    // Collection header data
    val collection: Collection? = null,
    val isLoadingCollection: Boolean = false,
    val collectionError: String? = null,
    // Tab selection
    val selectedTab: CollectionViewTab = CollectionViewTab.Items,
    // Items tab state
    val items: List<Summary> = emptyList(),
    val isLoadingItems: Boolean = false,
    val itemsError: String? = null,
    val itemsPage: Int = 0,
    val hasMoreItems: Boolean = true,
    // Settings tab state
    val isUpdating: Boolean = false,
    val updateError: String? = null,
    val updateSuccess: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    // Sharing tab state
    val collaborators: List<CollectionAcl> = emptyList(),
    val isLoadingCollaborators: Boolean = false,
    val collaboratorsError: String? = null,
    val inviteLink: CollectionInvite? = null,
    val isCreatingInvite: Boolean = false,
    val inviteError: String? = null,
    // Permissions (derived from collection type and user role)
    val isSystemCollection: Boolean = false,
    val canEdit: Boolean = false,
    val canShare: Boolean = false,
)

enum class CollectionViewTab {
    Items,
    Settings,
    Sharing,
}
