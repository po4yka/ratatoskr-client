package com.po4yka.bitesizereader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.po4yka.bitesizereader.domain.model.CollaboratorRole
import com.po4yka.bitesizereader.domain.model.CollectionType
import com.po4yka.bitesizereader.domain.usecase.CreateInviteLinkUseCase
import com.po4yka.bitesizereader.domain.usecase.DeleteCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionAclUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionItemsUseCase
import com.po4yka.bitesizereader.domain.usecase.GetCollectionUseCase
import com.po4yka.bitesizereader.domain.usecase.ManageCollaboratorUseCase
import com.po4yka.bitesizereader.domain.usecase.UpdateCollectionUseCase
import com.po4yka.bitesizereader.presentation.state.CollectionViewState
import com.po4yka.bitesizereader.presentation.state.CollectionViewTab
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

private const val PAGE_SIZE = 20

@Factory
class CollectionViewViewModel(
    private val getCollectionUseCase: GetCollectionUseCase,
    private val getCollectionItemsUseCase: GetCollectionItemsUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val getCollectionAclUseCase: GetCollectionAclUseCase,
    private val manageCollaboratorUseCase: ManageCollaboratorUseCase,
    private val createInviteLinkUseCase: CreateInviteLinkUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CollectionViewState())
    val state = _state.asStateFlow()

    private var collectionId: String = ""
    private var itemsLoaded = false
    private var collaboratorsLoaded = false

    fun loadCollection(id: String) {
        collectionId = id
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCollection = true, collectionError = null)
            try {
                val collection = getCollectionUseCase(id)
                val isSystem = collection?.type == CollectionType.System
                val canEdit = !isSystem
                val canShare = !isSystem

                _state.value =
                    _state.value.copy(
                        collection = collection,
                        isLoadingCollection = false,
                        isSystemCollection = isSystem,
                        canEdit = canEdit,
                        canShare = canShare,
                    )

                // Load items for the default tab
                loadItems()
            } catch (e: Exception) {
                logger.error(e) { "Error loading collection $id" }
                _state.value =
                    _state.value.copy(
                        isLoadingCollection = false,
                        collectionError = e.message ?: "Failed to load collection",
                    )
            }
        }
    }

    fun selectTab(tab: CollectionViewTab) {
        if (_state.value.selectedTab == tab) return

        _state.value = _state.value.copy(selectedTab = tab)

        // Lazy load data for the selected tab
        when (tab) {
            CollectionViewTab.Items -> {
                if (!itemsLoaded) {
                    loadItems()
                }
            }
            CollectionViewTab.Settings -> {
                // No additional data to load for settings
            }
            CollectionViewTab.Sharing -> {
                if (!collaboratorsLoaded) {
                    loadCollaborators()
                }
            }
        }
    }

    fun loadItems() {
        if (collectionId.isEmpty()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingItems = true, itemsError = null)
            try {
                val items = getCollectionItemsUseCase(collectionId, PAGE_SIZE, 0)
                _state.value =
                    _state.value.copy(
                        items = items,
                        isLoadingItems = false,
                        itemsPage = 0,
                        hasMoreItems = items.size >= PAGE_SIZE,
                    )
                itemsLoaded = true
            } catch (e: Exception) {
                logger.error(e) { "Error loading items for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isLoadingItems = false,
                        itemsError = e.message ?: "Failed to load items",
                    )
            }
        }
    }

    fun loadMoreItems() {
        if (collectionId.isEmpty() || !_state.value.hasMoreItems || _state.value.isLoadingItems) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingItems = true)
            try {
                val nextPage = _state.value.itemsPage + 1
                val offset = nextPage * PAGE_SIZE
                val newItems = getCollectionItemsUseCase(collectionId, PAGE_SIZE, offset)
                _state.value =
                    _state.value.copy(
                        items = _state.value.items + newItems,
                        isLoadingItems = false,
                        itemsPage = nextPage,
                        hasMoreItems = newItems.size >= PAGE_SIZE,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error loading more items for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isLoadingItems = false,
                        itemsError = e.message ?: "Failed to load more items",
                    )
            }
        }
    }

    fun updateCollection(
        name: String? = null,
        description: String? = null,
        isPublic: Boolean? = null,
    ) {
        if (collectionId.isEmpty() || !_state.value.canEdit) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true, updateError = null, updateSuccess = false)
            try {
                val updatedCollection = updateCollectionUseCase(collectionId, name, description, isPublic)
                _state.value =
                    _state.value.copy(
                        collection = updatedCollection,
                        isUpdating = false,
                        updateSuccess = true,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error updating collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isUpdating = false,
                        updateError = e.message ?: "Failed to update collection",
                    )
            }
        }
    }

    fun deleteCollection(onDeleted: () -> Unit) {
        if (collectionId.isEmpty() || !_state.value.canEdit) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true, deleteError = null)
            try {
                deleteCollectionUseCase(collectionId)
                _state.value = _state.value.copy(isDeleting = false)
                onDeleted()
            } catch (e: Exception) {
                logger.error(e) { "Error deleting collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isDeleting = false,
                        deleteError = e.message ?: "Failed to delete collection",
                    )
            }
        }
    }

    fun loadCollaborators() {
        if (collectionId.isEmpty()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCollaborators = true, collaboratorsError = null)
            try {
                val collaborators = getCollectionAclUseCase(collectionId)
                _state.value =
                    _state.value.copy(
                        collaborators = collaborators,
                        isLoadingCollaborators = false,
                    )
                collaboratorsLoaded = true
            } catch (e: Exception) {
                logger.error(e) { "Error loading collaborators for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isLoadingCollaborators = false,
                        collaboratorsError = e.message ?: "Failed to load collaborators",
                    )
            }
        }
    }

    fun addCollaborator(
        userId: Int,
        role: CollaboratorRole,
    ) {
        if (collectionId.isEmpty() || !_state.value.canShare) return

        viewModelScope.launch {
            try {
                manageCollaboratorUseCase.addCollaborator(collectionId, userId, role)
                // Reload collaborators to get the updated list
                loadCollaborators()
            } catch (e: Exception) {
                logger.error(e) { "Error adding collaborator to collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        collaboratorsError = e.message ?: "Failed to add collaborator",
                    )
            }
        }
    }

    fun removeCollaborator(userId: Int) {
        if (collectionId.isEmpty() || !_state.value.canShare) return

        viewModelScope.launch {
            try {
                manageCollaboratorUseCase.removeCollaborator(collectionId, userId)
                // Remove from local state immediately
                _state.value =
                    _state.value.copy(
                        collaborators = _state.value.collaborators.filter { it.userId != userId },
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error removing collaborator from collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        collaboratorsError = e.message ?: "Failed to remove collaborator",
                    )
            }
        }
    }

    fun createInviteLink(role: CollaboratorRole) {
        if (collectionId.isEmpty() || !_state.value.canShare) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isCreatingInvite = true, inviteError = null)
            try {
                val invite = createInviteLinkUseCase(collectionId, role)
                _state.value =
                    _state.value.copy(
                        inviteLink = invite,
                        isCreatingInvite = false,
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error creating invite link for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        isCreatingInvite = false,
                        inviteError = e.message ?: "Failed to create invite link",
                    )
            }
        }
    }

    fun clearUpdateSuccess() {
        _state.value = _state.value.copy(updateSuccess = false)
    }

    fun clearInviteLink() {
        _state.value = _state.value.copy(inviteLink = null)
    }
}
