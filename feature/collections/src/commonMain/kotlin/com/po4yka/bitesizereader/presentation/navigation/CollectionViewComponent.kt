package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionViewViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface CollectionViewComponent {
    val viewModel: CollectionViewViewModel
    val collectionId: String

    fun onBackClicked()

    fun onSummaryClicked(summaryId: String)

    fun onDeleteConfirmed()
}

class DefaultCollectionViewComponent(
    componentContext: ComponentContext,
    override val collectionId: String,
    private val onBack: () -> Unit,
    private val onNavigateToSummary: (String) -> Unit,
    private val onCollectionDeleted: () -> Unit,
) : CollectionViewComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: CollectionViewViewModel =
        retainedInstance {
            get<CollectionViewViewModel>().also { it.loadCollection(collectionId) }
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
