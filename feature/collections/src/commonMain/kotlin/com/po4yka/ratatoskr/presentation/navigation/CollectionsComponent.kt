package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.ratatoskr.presentation.viewmodel.CollectionsViewModel

interface CollectionsComponent {
    val viewModel: CollectionsViewModel

    fun onCollectionClicked(id: String)
}

class DefaultCollectionsComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> CollectionsViewModel,
    private val onCollectionSelected: (String) -> Unit,
) : CollectionsComponent, ComponentContext by componentContext {
    override val viewModel: CollectionsViewModel = retainedInstance { viewModelFactory() }

    override fun onCollectionClicked(id: String) {
        onCollectionSelected(id)
    }
}
