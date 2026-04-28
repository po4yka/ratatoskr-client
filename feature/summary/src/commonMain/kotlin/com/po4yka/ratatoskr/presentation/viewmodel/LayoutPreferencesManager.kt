package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.presentation.state.LayoutMode
import com.po4yka.ratatoskr.presentation.state.SummaryListState
import com.po4yka.ratatoskr.presentation.state.ViewDensity

class LayoutPreferencesManager(
    private val stateAccessor: StateAccessor<SummaryListState>,
) {
    fun setLayoutMode(mode: LayoutMode) {
        stateAccessor.update { it.copy(layout = it.layout.copy(layoutMode = mode)) }
    }

    fun setViewDensity(density: ViewDensity) {
        stateAccessor.update { it.copy(layout = it.layout.copy(viewDensity = density)) }
    }
}
