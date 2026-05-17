package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.CollaboratorRole
import com.po4yka.ratatoskr.domain.model.CollectionType
import com.po4yka.ratatoskr.domain.usecase.CreateInviteLinkUseCase
import com.po4yka.ratatoskr.domain.usecase.DeleteCollectionUseCase
import com.po4yka.ratatoskr.domain.usecase.GetCollectionAclUseCase
import com.po4yka.ratatoskr.domain.usecase.GetCollectionItemsUseCase
import com.po4yka.ratatoskr.domain.usecase.GetCollectionUseCase
import com.po4yka.ratatoskr.domain.usecase.AddCollaboratorUseCase
import com.po4yka.ratatoskr.domain.usecase.RemoveCollaboratorUseCase
import com.po4yka.ratatoskr.domain.usecase.UpdateCollectionUseCase
import com.po4yka.ratatoskr.presentation.state.CollectionViewState
import com.po4yka.ratatoskr.presentation.state.CollectionViewTab
import com.po4yka.ratatoskr.util.error.runCatchingDomain
import com.po4yka.ratatoskr.util.error.toUserMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

private const val PAGE_SIZE = 20

@Suppress("TooManyFunctions", "LongParameterList") // ViewModel with complete collection management API
class CollectionViewViewModel(
    private val getCollectionUseCase: GetCollectionUseCase,
    private val getCollectionItemsUseCase: GetCollectionItemsUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val getCollectionAclUseCase: GetCollectionAclUseCase,
    private val addCollaboratorUseCase: AddCollaboratorUseCase,
    private val removeCollaboratorUseCase: RemoveCollaboratorUseCase,
    private val createInviteLinkUseCase: CreateInviteLinkUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CollectionViewState())
    val state = _state.asStateFlow()

    private var collectionId: String = ""
    private var itemsLoaded = false
    private var collaboratorsLoaded = false

    fun loadCollection(id: String) {
        collectionId = id
        viewModelScope.launch {
            _state.update {
                it.copy(
                    header = it.header.copy(isLoading = true, error = null),
                )
            }
            runCatchingDomain { getCollectionUseCase(id) }
                .onSuccess { collection ->
                    val isSystem = collection.type == CollectionType.System
                    val canEdit = !isSystem
                    val canShare = !isSystem
                    _state.update {
                        it.copy(
                            header =
                                it.header.copy(
                                    collection = collection,
                                    isLoading = false,
                                    isSystemCollection = isSystem,
                                    canEdit = canEdit,
                                    canShare = canShare,
                                ),
                        )
                    }
                    loadItems()
                }
                .onFailure { e ->
                    logger.error(e) { "Error loading collection $id" }
                    _state.update {
                        it.copy(
                            header =
                                it.header.copy(
                                    isLoading = false,
                                    error = e.toUserMessage("Failed to load collection"),
                                ),
                        )
                    }
                }
        }
    }

    fun selectTab(tab: CollectionViewTab) {
        if (_state.value.selectedTab == tab) return

        _state.update { it.copy(selectedTab = tab) }

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
            _state.update {
                it.copy(
                    items = it.items.copy(isLoading = true, error = null),
                )
            }
            runCatchingDomain { getCollectionItemsUseCase(collectionId, PAGE_SIZE, 0) }
                .onSuccess { items ->
                    _state.update {
                        it.copy(
                            items =
                                it.items.copy(
                                    items = items,
                                    isLoading = false,
                                    page = 0,
                                    hasMore = items.size >= PAGE_SIZE,
                                ),
                        )
                    }
                    itemsLoaded = true
                }
                .onFailure { e ->
                    logger.error(e) { "Error loading items for collection $collectionId" }
                    _state.update {
                        it.copy(
                            items =
                                it.items.copy(
                                    isLoading = false,
                                    error = e.toUserMessage("Failed to load items"),
                                ),
                        )
                    }
                }
        }
    }

    fun loadMoreItems() {
        if (collectionId.isEmpty() || !_state.value.items.hasMore || _state.value.items.isLoading) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    items = it.items.copy(isLoading = true),
                )
            }
            val nextPage = _state.value.items.page + 1
            val offset = nextPage * PAGE_SIZE
            runCatchingDomain { getCollectionItemsUseCase(collectionId, PAGE_SIZE, offset) }
                .onSuccess { newItems ->
                    _state.update {
                        it.copy(
                            items =
                                it.items.copy(
                                    items = it.items.items + newItems,
                                    isLoading = false,
                                    page = nextPage,
                                    hasMore = newItems.size >= PAGE_SIZE,
                                ),
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Error loading more items for collection $collectionId" }
                    _state.update {
                        it.copy(
                            items =
                                it.items.copy(
                                    isLoading = false,
                                    error = e.toUserMessage("Failed to load more items"),
                                ),
                        )
                    }
                }
        }
    }

    fun updateCollection(
        name: String? = null,
        description: String? = null,
    ) {
        if (collectionId.isEmpty() || !_state.value.header.canEdit) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    settings =
                        it.settings.copy(
                            isUpdating = true,
                            updateError = null,
                            updateSuccess = false,
                        ),
                )
            }
            runCatchingDomain { updateCollectionUseCase(collectionId, name, description) }
                .onSuccess { updatedCollection ->
                    _state.update {
                        it.copy(
                            header = it.header.copy(collection = updatedCollection),
                            settings = it.settings.copy(isUpdating = false, updateSuccess = true),
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Error updating collection $collectionId" }
                    _state.update {
                        it.copy(
                            settings =
                                it.settings.copy(
                                    isUpdating = false,
                                    updateError = e.toUserMessage("Failed to update collection"),
                                ),
                        )
                    }
                }
        }
    }

    fun deleteCollection(onDeleted: () -> Unit) {
        if (collectionId.isEmpty() || !_state.value.header.canEdit) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    settings = it.settings.copy(isDeleting = true, deleteError = null),
                )
            }
            runCatchingDomain { deleteCollectionUseCase(collectionId) }
                .onSuccess {
                    _state.update {
                        it.copy(
                            settings = it.settings.copy(isDeleting = false),
                        )
                    }
                    onDeleted()
                }
                .onFailure { e ->
                    logger.error(e) { "Error deleting collection $collectionId" }
                    _state.update {
                        it.copy(
                            settings =
                                it.settings.copy(
                                    isDeleting = false,
                                    deleteError = e.toUserMessage("Failed to delete collection"),
                                ),
                        )
                    }
                }
        }
    }

    fun loadCollaborators() {
        if (collectionId.isEmpty()) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    sharing = it.sharing.copy(isLoading = true, error = null),
                )
            }
            runCatchingDomain { getCollectionAclUseCase(collectionId) }
                .onSuccess { collaborators ->
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    collaborators = collaborators,
                                    isLoading = false,
                                ),
                        )
                    }
                    collaboratorsLoaded = true
                }
                .onFailure { e ->
                    logger.error(e) { "Error loading collaborators for collection $collectionId" }
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    isLoading = false,
                                    error = e.toUserMessage("Failed to load collaborators"),
                                ),
                        )
                    }
                }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun addCollaborator(
        userId: Int,
        role: CollaboratorRole,
    ) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            runCatchingDomain { addCollaboratorUseCase(collectionId, userId, role) }
                .onSuccess {
                    // Reload collaborators to get the updated list
                    loadCollaborators()
                }
                .onFailure { e ->
                    logger.error(e) { "Error adding collaborator to collection $collectionId" }
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    error = e.toUserMessage("Failed to add collaborator"),
                                ),
                        )
                    }
                }
        }
    }

    fun removeCollaborator(userId: Int) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            runCatchingDomain { removeCollaboratorUseCase(collectionId, userId) }
                .onSuccess {
                    // Remove from local state immediately
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    collaborators = it.sharing.collaborators.filter { it.userId != userId },
                                ),
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Error removing collaborator from collection $collectionId" }
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    error = e.toUserMessage("Failed to remove collaborator"),
                                ),
                        )
                    }
                }
        }
    }

    fun createInviteLink(role: CollaboratorRole) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    sharing = it.sharing.copy(isCreatingInvite = true, inviteError = null),
                )
            }
            runCatchingDomain { createInviteLinkUseCase(collectionId, role) }
                .onSuccess { invite ->
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    inviteLink = invite,
                                    isCreatingInvite = false,
                                ),
                        )
                    }
                }
                .onFailure { e ->
                    logger.error(e) { "Error creating invite link for collection $collectionId" }
                    _state.update {
                        it.copy(
                            sharing =
                                it.sharing.copy(
                                    isCreatingInvite = false,
                                    inviteError = e.toUserMessage("Failed to create invite link"),
                                ),
                        )
                    }
                }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun clearUpdateSuccess() {
        _state.update {
            it.copy(
                settings = it.settings.copy(updateSuccess = false),
            )
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun clearInviteLink() {
        _state.update {
            it.copy(
                sharing = it.sharing.copy(inviteLink = null),
            )
        }
    }
}
