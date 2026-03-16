package com.po4yka.bitesizereader.presentation.viewmodel

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

@Suppress("TooManyFunctions", "LongParameterList") // ViewModel with complete collection management API
@Factory
class CollectionViewViewModel(
    private val getCollectionUseCase: GetCollectionUseCase,
    private val getCollectionItemsUseCase: GetCollectionItemsUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val getCollectionAclUseCase: GetCollectionAclUseCase,
    private val manageCollaboratorUseCase: ManageCollaboratorUseCase,
    private val createInviteLinkUseCase: CreateInviteLinkUseCase,
) : BaseViewModel() {
    private val _state = MutableStateFlow(CollectionViewState())
    val state = _state.asStateFlow()

    private var collectionId: String = ""
    private var itemsLoaded = false
    private var collaboratorsLoaded = false

    @Suppress("TooGenericExceptionCaught")
    fun loadCollection(id: String) {
        collectionId = id
        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    header = _state.value.header.copy(isLoading = true, error = null),
                )
            try {
                val collection = getCollectionUseCase(id)
                val isSystem = collection?.type == CollectionType.System
                val canEdit = !isSystem
                val canShare = !isSystem

                _state.value =
                    _state.value.copy(
                        header =
                            _state.value.header.copy(
                                collection = collection,
                                isLoading = false,
                                isSystemCollection = isSystem,
                                canEdit = canEdit,
                                canShare = canShare,
                            ),
                    )

                // Load items for the default tab
                loadItems()
            } catch (e: Exception) {
                logger.error(e) { "Error loading collection $id" }
                _state.value =
                    _state.value.copy(
                        header =
                            _state.value.header.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load collection",
                            ),
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

    @Suppress("TooGenericExceptionCaught")
    fun loadItems() {
        if (collectionId.isEmpty()) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    items = _state.value.items.copy(isLoading = true, error = null),
                )
            try {
                val items = getCollectionItemsUseCase(collectionId, PAGE_SIZE, 0)
                _state.value =
                    _state.value.copy(
                        items =
                            _state.value.items.copy(
                                items = items,
                                isLoading = false,
                                page = 0,
                                hasMore = items.size >= PAGE_SIZE,
                            ),
                    )
                itemsLoaded = true
            } catch (e: Exception) {
                logger.error(e) { "Error loading items for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        items =
                            _state.value.items.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load items",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadMoreItems() {
        if (collectionId.isEmpty() || !_state.value.items.hasMore || _state.value.items.isLoading) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    items = _state.value.items.copy(isLoading = true),
                )
            try {
                val nextPage = _state.value.items.page + 1
                val offset = nextPage * PAGE_SIZE
                val newItems = getCollectionItemsUseCase(collectionId, PAGE_SIZE, offset)
                _state.value =
                    _state.value.copy(
                        items =
                            _state.value.items.copy(
                                items = _state.value.items.items + newItems,
                                isLoading = false,
                                page = nextPage,
                                hasMore = newItems.size >= PAGE_SIZE,
                            ),
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error loading more items for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        items =
                            _state.value.items.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load more items",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun updateCollection(
        name: String? = null,
        description: String? = null,
    ) {
        if (collectionId.isEmpty() || !_state.value.header.canEdit) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    settings =
                        _state.value.settings.copy(
                            isUpdating = true,
                            updateError = null,
                            updateSuccess = false,
                        ),
                )
            try {
                val updatedCollection = updateCollectionUseCase(collectionId, name, description)
                _state.value =
                    _state.value.copy(
                        header = _state.value.header.copy(collection = updatedCollection),
                        settings = _state.value.settings.copy(isUpdating = false, updateSuccess = true),
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error updating collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        settings =
                            _state.value.settings.copy(
                                isUpdating = false,
                                updateError = e.message ?: "Failed to update collection",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun deleteCollection(onDeleted: () -> Unit) {
        if (collectionId.isEmpty() || !_state.value.header.canEdit) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    settings = _state.value.settings.copy(isDeleting = true, deleteError = null),
                )
            try {
                deleteCollectionUseCase(collectionId)
                _state.value =
                    _state.value.copy(
                        settings = _state.value.settings.copy(isDeleting = false),
                    )
                onDeleted()
            } catch (e: Exception) {
                logger.error(e) { "Error deleting collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        settings =
                            _state.value.settings.copy(
                                isDeleting = false,
                                deleteError = e.message ?: "Failed to delete collection",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun loadCollaborators() {
        if (collectionId.isEmpty()) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    sharing = _state.value.sharing.copy(isLoading = true, error = null),
                )
            try {
                val collaborators = getCollectionAclUseCase(collectionId)
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                collaborators = collaborators,
                                isLoading = false,
                            ),
                    )
                collaboratorsLoaded = true
            } catch (e: Exception) {
                logger.error(e) { "Error loading collaborators for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                isLoading = false,
                                error = e.message ?: "Failed to load collaborators",
                            ),
                    )
            }
        }
    }

    @Suppress("unused", "TooGenericExceptionCaught") // Public API for UI layer
    fun addCollaborator(
        userId: Int,
        role: CollaboratorRole,
    ) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            try {
                manageCollaboratorUseCase.addCollaborator(collectionId, userId, role)
                // Reload collaborators to get the updated list
                loadCollaborators()
            } catch (e: Exception) {
                logger.error(e) { "Error adding collaborator to collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                error = e.message ?: "Failed to add collaborator",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun removeCollaborator(userId: Int) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            try {
                manageCollaboratorUseCase.removeCollaborator(collectionId, userId)
                // Remove from local state immediately
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                collaborators = _state.value.sharing.collaborators.filter { it.userId != userId },
                            ),
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error removing collaborator from collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                error = e.message ?: "Failed to remove collaborator",
                            ),
                    )
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun createInviteLink(role: CollaboratorRole) {
        if (collectionId.isEmpty() || !_state.value.header.canShare) return

        viewModelScope.launch {
            _state.value =
                _state.value.copy(
                    sharing = _state.value.sharing.copy(isCreatingInvite = true, inviteError = null),
                )
            try {
                val invite = createInviteLinkUseCase(collectionId, role)
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                inviteLink = invite,
                                isCreatingInvite = false,
                            ),
                    )
            } catch (e: Exception) {
                logger.error(e) { "Error creating invite link for collection $collectionId" }
                _state.value =
                    _state.value.copy(
                        sharing =
                            _state.value.sharing.copy(
                                isCreatingInvite = false,
                                inviteError = e.message ?: "Failed to create invite link",
                            ),
                    )
            }
        }
    }

    @Suppress("unused") // Public API for UI layer
    fun clearUpdateSuccess() {
        _state.value =
            _state.value.copy(
                settings = _state.value.settings.copy(updateSuccess = false),
            )
    }

    @Suppress("unused") // Public API for UI layer
    fun clearInviteLink() {
        _state.value =
            _state.value.copy(
                sharing = _state.value.sharing.copy(inviteLink = null),
            )
    }
}
