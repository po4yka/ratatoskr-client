package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.CollectionsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface CollectionsComponent {
    val viewModel: CollectionsViewModel

    fun onCollectionClicked(id: String)
}

class DefaultCollectionsComponent(
    componentContext: ComponentContext,
    private val onCollectionSelected: (String) -> Unit,
) : CollectionsComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: CollectionsViewModel = retainedInstance { get() }

    override fun onCollectionClicked(id: String) {
        onCollectionSelected(id)
    }
}
