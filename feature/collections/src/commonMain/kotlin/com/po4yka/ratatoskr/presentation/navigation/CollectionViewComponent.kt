package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.ratatoskr.presentation.viewmodel.CollectionViewViewModel

interface CollectionViewComponent {
    val viewModel: CollectionViewViewModel
    val collectionId: String

    fun onBackClicked()

    fun onSummaryClicked(summaryId: String)

    fun onDeleteConfirmed()
}

class DefaultCollectionViewComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> CollectionViewViewModel,
    override val collectionId: String,
    private val onBack: () -> Unit,
    private val onNavigateToSummary: (String) -> Unit,
    private val onCollectionDeleted: () -> Unit,
) : CollectionViewComponent, ComponentContext by componentContext {
    override val viewModel: CollectionViewViewModel =
        retainedInstance {
            viewModelFactory().also { it.loadCollection(collectionId) }
        }

    override fun onBackClicked() {
        onBack()
    }

    override fun onSummaryClicked(summaryId: String) {
        onNavigateToSummary(summaryId)
    }

    override fun onDeleteConfirmed() {
        viewModel.deleteCollection {
            onCollectionDeleted()
        }
    }
}
