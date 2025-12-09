package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.CollectionViewViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface CollectionViewComponent {
    val viewModel: CollectionViewViewModel
    val collectionId: String

    fun onBackClicked()

    fun onSummaryClicked(summaryId: String)

    fun onDeleteConfirmed()
}

class DefaultCollectionViewComponent(
    override val collectionId: String,
    private val onBack: () -> Unit,
    private val onNavigateToSummary: (String) -> Unit,
    private val onCollectionDeleted: () -> Unit,
) : CollectionViewComponent, KoinComponent {
    override val viewModel: CollectionViewViewModel by inject()

    init {
        viewModel.loadCollection(collectionId)
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
