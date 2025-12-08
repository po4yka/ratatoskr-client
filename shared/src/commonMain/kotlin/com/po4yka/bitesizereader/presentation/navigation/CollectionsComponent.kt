package com.po4yka.bitesizereader.presentation.navigation

interface CollectionsComponent {
    fun onCollectionClicked(id: String)
}

class DefaultCollectionsComponent(
    private val onCollectionSelected: (String) -> Unit
) : CollectionsComponent {
    override fun onCollectionClicked(id: String) {
        onCollectionSelected(id)
    }
}
