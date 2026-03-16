package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.presentation.state.LayoutMode
import com.po4yka.bitesizereader.presentation.state.SummaryListState
import com.po4yka.bitesizereader.presentation.state.ViewDensity

class LayoutPreferencesManager(
    private val stateAccessor: StateAccessor<SummaryListState>,
) {
    fun setLayoutMode(mode: LayoutMode) {
        stateAccessor.update { it.copy(layoutMode = mode) }
    }

    fun setViewDensity(density: ViewDensity) {
        stateAccessor.update { it.copy(viewDensity = density) }
    }
}
