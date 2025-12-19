package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext

interface CollectionsComponent {
    fun onCollectionClicked(id: String)
}

class DefaultCollectionsComponent(
    componentContext: ComponentContext,
    private val onCollectionSelected: (String) -> Unit,
) : CollectionsComponent, ComponentContext by componentContext {
    override fun onCollectionClicked(id: String) {
        onCollectionSelected(id)
    }
}
