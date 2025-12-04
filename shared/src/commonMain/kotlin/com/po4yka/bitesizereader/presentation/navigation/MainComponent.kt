package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext

interface MainComponent {
    val summaryListComponent: SummaryListComponent
}

class DefaultMainComponent(
    componentContext: ComponentContext
) : MainComponent, ComponentContext by componentContext {
    override val summaryListComponent = DefaultSummaryListComponent { id ->
        // TODO: Navigate to detail
    }
}